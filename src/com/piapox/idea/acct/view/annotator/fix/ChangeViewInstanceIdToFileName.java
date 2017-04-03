package com.piapox.idea.acct.view.annotator.fix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.GenericAttributeValue;
import com.piapox.idea.acct.util.FileHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class ChangeViewInstanceIdToFileName extends BaseIntentionAction {

    private String fileNameWithoutExtension;
    private GenericAttributeValue<String> instanceIdAttributeValue;

    public ChangeViewInstanceIdToFileName(String fileNameWithoutExtension, GenericAttributeValue<String> instanceIdAttributeValue) {
        this.fileNameWithoutExtension = fileNameWithoutExtension;
        this.instanceIdAttributeValue = instanceIdAttributeValue;
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
        return "Change view instance id to file name.";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        FileHelper.runWriteAction(()->{
            instanceIdAttributeValue.setValue(fileNameWithoutExtension);
        }, project, file.getVirtualFile());
    }
}
