package com.piapox.idea.acct.autoReload;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.piapox.idea.acct.projectType.CodeLineConfigProject;
import com.piapox.idea.acct.projectType.ConfigProject;
import com.piapox.idea.acct.projectType.ConfigProjectFactory;
import com.piapox.idea.acct.util.FileHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.piapox.idea.acct.autoReload.settings.AutoReloadSettingsDAO.loadSettings;
import static com.piapox.idea.acct.util.UiHelper.alert;

/**
 * Detect config xml file changes in bulk
 */
public class ConfigBulkFileListener extends BulkFileListener.Adapter {

    private final Project project;
    private final ConfigProject configProject;

    public ConfigBulkFileListener(Project project) {
        this.project = project;
        this.configProject = ConfigProjectFactory.getConfigProject(project);
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        // source path contains everything till config type name, e.g.
        // U:/ALL/dev/s/cop/base/copernicus/server/portal-config/src/main/webapp/WEB-INF/config/classic/views
        Set<String> sourceAbsPaths = new HashSet<>();
        Set<String> sourceCopPaths = new HashSet<>();

        collectChanges(events, sourceAbsPaths, sourceCopPaths);

        if (configProject instanceof CodeLineConfigProject) {
            copySourceToTargetDeployFolder(sourceAbsPaths, sourceCopPaths);
        }

        reloadConfigs(sourceAbsPaths, sourceCopPaths);
    }

    private void reloadConfigs(Set<String> sourceAbsPaths, Set<String> sourceCopPaths) {
        ConfigReloader configReloader = new ConfigReloader(loadSettings(project));

        if (sourceAbsPaths.size() > 0) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    configReloader.reloadAbsConfig(new ConfigReloader.ConfigReloadedListener() {
                        @Override
                        public void onSuccess() {
                            alert("Abs configurations reloaded.");
                            triggerLiveReload();
                        }

                        @Override
                        public void onFailed() {
                            alert("Abs configurations reload failed.");
                        }
                    });
                }
            });
        }

        if (sourceCopPaths.size() > 0) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    configReloader.reloadCopConfig(new ConfigReloader.ConfigReloadedListener() {
                        @Override
                        public void onSuccess() {
                            alert("Cop configurations reloaded.");
                            triggerLiveReload();
                        }

                        @Override
                        public void onFailed() {
                            alert("Cop configurations reload failed.");
                        }
                    });
                }
            });
        }
    }

    private void triggerLiveReload() {
        String triggerFilePath = loadSettings(project).getLiveLoadTriggerFile();
        if (isNullOrEmpty(triggerFilePath)) return;

        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        VirtualFile triggerFile = fileSystem.findFileByPath(triggerFilePath);
        if (triggerFile != null) {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    triggerFile.setBinaryContent(longToByteArray(System.currentTimeMillis()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * http://stackoverflow.com/a/4299165/1943272
     */
    private byte[] longToByteArray(long longValue) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(longValue).array();
    }

    private void collectChanges(@NotNull List<? extends VFileEvent> events, Set<String> sourceAbsPaths, Set<String> sourceCopPaths) {
        events.forEach(e -> {
            // doesn't matter what event it is, only collect the related file path
            //UiHelper.alert(e.getClass().toString() + "  " + e.getPath());
            String filePath = e.getPath();
            String configSetName = FileHelper.getConfigSetName(filePath);
            String configTypeName = FileHelper.getConfigTypeName(filePath);
            if (configSetName != null && configTypeName != null) {
                VirtualFile absConfigFolder = configProject.getAbsConfigFolder(configSetName);
                VirtualFile copConfigFolder = configProject.getCopConfigFolder(configSetName);
                if (absConfigFolder != null) {
                    String absConfigPath = absConfigFolder.getCanonicalPath();
                    if (absConfigPath != null && filePath.startsWith(absConfigPath)) {
                        sourceAbsPaths.add(Paths.get(absConfigPath, configTypeName).toString());
                    }
                }
                if (copConfigFolder != null) {
                    String copConfigPath = copConfigFolder.getCanonicalPath();
                    if (copConfigPath != null && filePath.startsWith(copConfigPath)) {
                        sourceCopPaths.add(Paths.get(copConfigPath, configTypeName).toString());
                    }
                }
            }
        });
    }

    private void copySourceToTargetDeployFolder(Set<String> sourceAbsPaths, Set<String> sourceCopPaths) {
        LocalFileSystem fileSystem = LocalFileSystem.getInstance();

        String absTargetPath = loadSettings(project).getABSTargetPath();
        if (!isNullOrEmpty(absTargetPath)) {
            sourceAbsPaths.forEach(sourcePath -> {
                String targetPath = Paths.get(absTargetPath, sourcePath.substring(sourcePath.lastIndexOf("WEB-INF"))).toString();
                VirtualFile source = fileSystem.findFileByPath(sourcePath);
                VirtualFile target = fileSystem.findFileByPath(targetPath);
                if (source != null && target != null) {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                target.delete(this);
                                source.copy(this, target.getParent(), target.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }

        String copTargetPath = loadSettings(project).getCOPTargetPath();
        if (!isNullOrEmpty(copTargetPath)) {
            sourceCopPaths.forEach(sourcePath -> {
                String targetPath = Paths.get(copTargetPath, sourcePath.substring(sourcePath.lastIndexOf("WEB-INF"))).toString();
                VirtualFile source = fileSystem.findFileByPath(sourcePath);
                VirtualFile target = fileSystem.findFileByPath(targetPath);
                if (source != null && target != null) {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                target.delete(this);
                                source.copy(this, target.getParent(), target.getName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }
}
