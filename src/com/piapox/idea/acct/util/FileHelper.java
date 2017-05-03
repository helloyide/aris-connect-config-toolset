package com.piapox.idea.acct.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.piapox.idea.acct.projectType.ConfigProject;
import com.piapox.idea.acct.projectType.ConfigProjectFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileHelper {

    /**
     * For example
     * "C:/SoftwareAG/ARIS10.0/server/bin/work/work_copernicus_s/base/webapps/ROOT/WEB-INF/config/classic/views/home.xml"
     * returns "views"
     */
    @Nullable
    public static String getConfigTypeName(VirtualFile virtualFile) {
        if (virtualFile == null) return null;
        String canonicalPath = virtualFile.getCanonicalPath();
        return getConfigTypeName(canonicalPath);
    }

    /**
     * For example
     * "C:/SoftwareAG/ARIS10.0/server/bin/work/work_copernicus_s/base/webapps/ROOT/WEB-INF/config/classic/views/home.xml"
     * returns "views"
     */
    @Nullable
    public static String getConfigTypeName(String filePath) {
        if (filePath != null) {
            String[] tokens = filePath.split("/");
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].equals("WEB-INF")) {
                    if (i + 1 < tokens.length && tokens[i + 1].equals("config")) {
                        if (i + 3 < tokens.length) {
                            return tokens[i + 3];
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * For example
     * "C:/SoftwareAG/ARIS10.0/server/bin/work/work_copernicus_s/base/webapps/ROOT/WEB-INF/config/classic/views/home.xml"
     * returns "classic"
     */
    @Nullable
    public static String getConfigSetName(VirtualFile virtualFile) {
        if (virtualFile == null) return null;
        String canonicalPath = virtualFile.getCanonicalPath();
        return getConfigSetName(canonicalPath);
    }

    /**
     * For example
     * "C:/SoftwareAG/ARIS10.0/server/bin/work/work_copernicus_s/base/webapps/ROOT/WEB-INF/config/classic/views/home.xml"
     * returns "classic"
     */
    @Nullable
    public static String getConfigSetName(String filePath) {
        if (filePath != null) {
            String[] tokens = filePath.split("/");
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].equals("WEB-INF")) {
                    if (i + 1 < tokens.length && tokens[i + 1].equals("config")) {
                        if (i + 2 < tokens.length) {
                            return tokens[i + 2];
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * E.g. "test.xml" => "test"
     */
    public static String getFileNameWithoutExtension(String fileNameWithExtension) {
        if (fileNameWithExtension.length() < 4)
            throw new IllegalStateException("fileNameWithExtension need 5 characters at least: " + fileNameWithExtension);
        return fileNameWithExtension.substring(0, fileNameWithExtension.length() - 4);
    }

    /**
     * Based on VirtualFile.findChildren()
     */
    @NotNull
    public static List<VirtualFile> findChildrenEndWith(@NotNull VirtualFile parent, @NotNull String ending) {
        VirtualFile[] children = parent.getChildren();
        if (children == null) return Collections.emptyList();

        List<VirtualFile> result = new ArrayList<>(children.length);
        for (VirtualFile child : children) {
            if (child.getName().endsWith(ending)) {
                result.add(child);
            }
        }
        return result;
    }

    @Nullable
    public static XmlFile toXmlFile(Project project, VirtualFile virtualFile) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile != null && psiFile instanceof XmlFile) {
            return (XmlFile) psiFile;
        }
        return null;
    }

    /**
     * @return if only found the *_default.xml
     */
    public static boolean collectInstanceConfigurations(
            Project project, String configSetName, String componentType, @Nullable String instanceId, List<VirtualFile> instanceConfigFiles) {
        boolean onlyDefault = false;
        ConfigProject configProject = ConfigProjectFactory.getConfigProject(project);
        VirtualFile copConfigFolder = configProject.getCopConfigFolder(configSetName);
        if (copConfigFolder != null) {
            onlyDefault = collectInstanceConfigFiles(componentType, instanceId, copConfigFolder, instanceConfigFiles);
        }

        if (instanceConfigFiles.isEmpty()) {
            VirtualFile absConfigFolder = configProject.getAbsConfigFolder(configSetName);
            if (absConfigFolder != null) {
                onlyDefault = collectInstanceConfigFiles(componentType, instanceId, absConfigFolder, instanceConfigFiles);
            }
        }
        return onlyDefault;
    }

    /**
     * @return if only found the *_default.xml
     */
    private static boolean collectInstanceConfigFiles(
            String componentType, @Nullable String instanceId, VirtualFile configFolder, List<VirtualFile> instanceConfigFiles) {
        boolean usingDefault = false;
        List<VirtualFile> found = new ArrayList<>();
        VirtualFile instancesFolder = configFolder.findChild("instances");
        if (instancesFolder != null) {
            VirtualFile componentFolder = instancesFolder.findChild(componentType);
            if (componentFolder != null) {
                if (instanceId != null) {
                    addIfNotNull(found, componentFolder.findChild(instanceId + ".xml"));
                    List<VirtualFile> matchedChildren = findChildrenEndWith(componentFolder, "-" + instanceId + ".xml");
                    matchedChildren.forEach(child -> addIfNotNull(found, child));
                }
                if (found.isEmpty()) {
                    VirtualFile defaultInstanceConfigFile = componentFolder.findChild(componentType + "_default.xml");
                    if (defaultInstanceConfigFile != null) {
                        found.add(defaultInstanceConfigFile);
                        usingDefault = true;
                    }
                }
            }
        }
        instanceConfigFiles.addAll(found);
        return usingDefault;
    }

    private static <T> void addIfNotNull(List<T> collection, @Nullable T child) {
        if (child == null) return;
        collection.add(child);
    }

    /**
     * All modifications to physical PSI, VFS and project model should be done in write actions.
     */
    public static void runWriteAction(Runnable runnable, Project project, VirtualFile... virtualFile) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            ReadonlyStatusHandler.OperationStatus status = ReadonlyStatusHandler.getInstance(project).ensureFilesWritable(virtualFile);
            if (!status.hasReadonlyFiles()) {
                runnable.run();
            }
        });
    }

    /**
     * All modifications to documents should be done in commands.
     */
    public static void runWriteCommand(Runnable runnable, Project project, VirtualFile... virtualFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            ReadonlyStatusHandler.OperationStatus status = ReadonlyStatusHandler.getInstance(project).ensureFilesWritable(virtualFile);
            if (!status.hasReadonlyFiles()) {
                runnable.run();
            }
        });
    }
}
