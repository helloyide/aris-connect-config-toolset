package com.piapox.idea.acct.projectType;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

public interface ConfigProject {

    @Nullable
    VirtualFile getCopConfigFolder(String configSet);

    @Nullable
    VirtualFile getAbsConfigFolder(String configSet);

    boolean isGlobalConfigAvailable();

}
