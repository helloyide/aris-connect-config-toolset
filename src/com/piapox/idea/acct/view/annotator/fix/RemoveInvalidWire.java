package com.piapox.idea.acct.view.annotator.fix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.piapox.idea.acct.util.FileHelper;
import com.piapox.idea.acct.view.element.Wire;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RemoveInvalidWire extends BaseIntentionAction {

    private Wire element;

    public RemoveInvalidWire(Wire element) {
        this.element = element;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "RemoveInvalidWire";
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove this invalid wire.";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        FileHelper.runWriteAction(()->{
            element.undefine();
        }, project, file.getVirtualFile());
    }
}
