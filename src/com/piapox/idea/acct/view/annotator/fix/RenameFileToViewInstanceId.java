package com.piapox.idea.acct.view.annotator.fix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.piapox.idea.acct.util.FileHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RenameFileToViewInstanceId extends BaseIntentionAction {

    private String viewInstanceId;

    public RenameFileToViewInstanceId(String viewInstanceId) {
        this.viewInstanceId = viewInstanceId;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "InstanceIdAndFileNameConflict";
    }

    @NotNull
    @Override
    public String getText() {
        return "Rename file to view instance name.";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        FileHelper.runWriteAction(()->{
            try {
                file.getVirtualFile().rename(RenameFileToViewInstanceId.this, viewInstanceId + ".xml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, project, file.getVirtualFile());
    }
}
