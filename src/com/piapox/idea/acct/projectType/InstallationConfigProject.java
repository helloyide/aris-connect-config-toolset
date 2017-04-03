package com.piapox.idea.acct.projectType;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static com.intellij.openapi.module.ModuleUtilCore.getModuleDirPath;

/**
 * User setup project with installed ARIS config, he should created 2 modules for copernicus and abs runnable
 */
public class InstallationConfigProject implements ConfigProject {

    private Project project;

    InstallationConfigProject(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public VirtualFile getCopConfigFolder(String configSet) {
        Optional<Module> copModuleOptional = Arrays.stream(ModuleManager.getInstance(project).getModules()).filter(this::isCopModule).findFirst();
        if (copModuleOptional.isPresent()) {
            Module module = copModuleOptional.get();
            VirtualFile moduleDir = LocalFileSystem.getInstance().findFileByPath(getModuleDirPath(module));
            return moduleDir != null ? moduleDir.findChild(configSet) : null;
        }
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getAbsConfigFolder(String configSet) {
        Optional<Module> absModuleOptional = Arrays.stream(ModuleManager.getInstance(project).getModules()).filter(this::isAbsModule).findFirst();
        if (absModuleOptional.isPresent()) {
            Module module = absModuleOptional.get();
            VirtualFile moduleDir = LocalFileSystem.getInstance().findFileByPath(getModuleDirPath(module));
            return moduleDir != null ? moduleDir.findChild(configSet) : null;
        }
        return null;
    }

    @Override
    public boolean isGlobalConfigAvailable() {
        return true;
    }

    private boolean isCopModule(Module module) {
        return !isAbsModule(module);
    }

    private boolean isAbsModule(Module module) {
        String moduleDirPath = getModuleDirPath(module);
        VirtualFile moduleDir = LocalFileSystem.getInstance().findFileByPath(moduleDirPath);
        return moduleDir != null && moduleDir.findFileByRelativePath("spring") != null;
    }
}
