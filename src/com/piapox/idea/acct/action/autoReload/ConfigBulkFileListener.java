package com.piapox.idea.acct.action.autoReload;

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
import com.piapox.idea.acct.util.UiHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Detect config xml file changes in bulk
 */
public class ConfigBulkFileListener extends BulkFileListener.Adapter {

    // TODO: these two value should be configurable with UI
    private static final String absConfigTargetPath = "U:/ALL/dev/s/cop/apps/aris/aris/server-complete/target/y-aris-server-complete-99.0.0.0-SNAPSHOT";
    private static final String copConfigTargetPath = "U:/ALL/dev/s/cop/base/copernicus/server/portal-server/target/portal-server-99.0.0.0-SNAPSHOT";
    private static final String TRIGGER_FILENAME = "trigger.txt";
    private static final String TRIGGER_FOLDER_PATH = "U:/liveReloadTrigger/";

    private final Project project;
    private final ConfigProject configProject;

    ConfigBulkFileListener(Project project) {
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
        if (sourceAbsPaths.size() > 0) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    ConfigReloader.reloadAbsConfig(new ConfigReloader.ConfigReloadedListener() {
                        @Override
                        public void onSuccess() {
                            UiHelper.alert("Abs configurations reloaded.");
                            triggerLiveReload();
                        }

                        @Override
                        public void onFailed() {
                            UiHelper.alert("Abs configurations reload failed.");
                        }
                    });
                }
            });
        }

        if (sourceCopPaths.size() > 0) {
            ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
                @Override
                public void run() {
                    ConfigReloader.reloadCopConfig(new ConfigReloader.ConfigReloadedListener() {
                        @Override
                        public void onSuccess() {
                            UiHelper.alert("Cop configurations reloaded.");
                            triggerLiveReload();
                        }

                        @Override
                        public void onFailed() {
                            UiHelper.alert("Cop configurations reload failed.");
                        }
                    });
                }
            });
        }
    }

    private void triggerLiveReload() {
        LocalFileSystem fileSystem = LocalFileSystem.getInstance();
        VirtualFile triggerFolder = fileSystem.findFileByPath(TRIGGER_FOLDER_PATH);
        if (triggerFolder == null) return;

        VirtualFile triggerFile = triggerFolder.findChild(TRIGGER_FILENAME);
        if (triggerFile == null) {
            // create the file
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    fileSystem.createChildFile(this, triggerFolder, TRIGGER_FILENAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
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
        sourceAbsPaths.forEach(sourcePath -> {
            String targetPath = Paths.get(absConfigTargetPath, sourcePath.substring(sourcePath.lastIndexOf("WEB-INF"))).toString();
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

        sourceCopPaths.forEach(sourcePath -> {
            String targetPath = Paths.get(copConfigTargetPath, sourcePath.substring(sourcePath.lastIndexOf("WEB-INF"))).toString();
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
