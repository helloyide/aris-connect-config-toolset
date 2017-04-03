package com.piapox.idea.acct.projectType;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;

/**
 * For developers which work with code lines
 */
public class CodeLineConfigProject implements ConfigProject {

    private static final String MODULE_NAME_COP = "copernicus-portal-config";
    private static final String MODULE_NAME_ABS = "cop-pub-config";
    private Project project;

    CodeLineConfigProject(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public VirtualFile getCopConfigFolder(String configSet) {
        Module module = ModuleManager.getInstance(project).findModuleByName(MODULE_NAME_COP);
        if (module != null) {
            String moduleDirPath = ModuleUtil.getModuleDirPath(module);
            String configSetPath = Paths.get(moduleDirPath, "src", "main", "webapp", "WEB-INF", "config", configSet).toString();
            return LocalFileSystem.getInstance().findFileByPath(configSetPath);
        }
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getAbsConfigFolder(String configSet) {
        Module module = ModuleManager.getInstance(project).findModuleByName(MODULE_NAME_ABS);
        if (module != null) {
            String moduleDirPath = ModuleUtil.getModuleDirPath(module);
            String configSetPath = Paths.get(moduleDirPath, "src", "main", "webapp", "WEB-INF", "config", configSet).toString();
            return LocalFileSystem.getInstance().findFileByPath(configSetPath);
        }
        return null;
    }

    @Override
    public boolean isGlobalConfigAvailable() {
        return true;
    }
}
