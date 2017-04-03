package com.piapox.idea.acct.projectType;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

public class ConfigProjectFactory {

    public static ConfigProject getConfigProject(Project project){
        Module[] modules = ModuleManager.getInstance(project).getModules();
        if (modules.length == 1) {
            return new ZipConfigProject(project);
        } else if (modules.length == 2) {
            return new InstallationConfigProject(project);
        } else {
            return new CodeLineConfigProject(project);
        }
    }

}
