package com.piapox.idea.acct.projectType;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * User downloads the configuration in web UI, it's a zip file which contains both abs and cop configurations
 */
public class ZipConfigProject implements ConfigProject {

    private Project project;

    ZipConfigProject(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public VirtualFile getCopConfigFolder(String configSet) {
        return null;
    }

    @Nullable
    @Override
    public VirtualFile getAbsConfigFolder(String configSet) {
        return null;
    }

    @Override
    public boolean isGlobalConfigAvailable() {
        return false;
    }
}
