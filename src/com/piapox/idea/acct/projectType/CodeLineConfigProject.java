package com.piapox.idea.acct.projectType;

import com.google.common.base.Strings;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;

import static com.piapox.idea.acct.autoReload.settings.AutoReloadSettingsDAO.loadSettings;

/**
 * For developers which work with code lines
 */
public class CodeLineConfigProject implements ConfigProject {

    private Project project;

    CodeLineConfigProject(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public VirtualFile getCopConfigFolder(@NotNull String configSet) {
        String moduleName = loadSettings(project).getCOPModuleName();
        if (!Strings.isNullOrEmpty(moduleName)) {
            Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
            if (module != null) {
                String moduleDirPath = ModuleUtil.getModuleDirPath(module);
                String configSetPath = Paths.get(moduleDirPath, "src", "main", "webapp", "WEB-INF", "config", configSet).toString();
                return LocalFileSystem.getInstance().findFileByPath(configSetPath);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getAbsConfigFolder(@NotNull String configSet) {
        String moduleName = loadSettings(project).getABSModuleName();
        if (!Strings.isNullOrEmpty(moduleName)) {
            Module module = ModuleManager.getInstance(project).findModuleByName(moduleName);
            if (module != null) {
                String moduleDirPath = ModuleUtil.getModuleDirPath(module);
                String configSetPath = Paths.get(moduleDirPath, "src", "main", "webapp", "WEB-INF", "config", configSet).toString();
                return LocalFileSystem.getInstance().findFileByPath(configSetPath);
            }
        }
        return null;
    }

    @Override
    public boolean isGlobalConfigAvailable() {
        return true;
    }
}
