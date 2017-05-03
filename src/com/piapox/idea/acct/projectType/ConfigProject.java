package com.piapox.idea.acct.projectType;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigProject {

    @Nullable
    VirtualFile getCopConfigFolder(@NotNull String configSet);

    @Nullable
    VirtualFile getAbsConfigFolder(@NotNull String configSet);

    boolean isGlobalConfigAvailable();

}
