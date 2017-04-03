package com.piapox.idea.acct.view.annotator.fix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.piapox.idea.acct.util.FileHelper;
import com.piapox.idea.acct.view.element.Property;
import com.piapox.idea.acct.view.element.ViewConfigElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class MakeFreeLayoutChildInvisible extends BaseIntentionAction {

    private ViewConfigElement element;

    public MakeFreeLayoutChildInvisible(ViewConfigElement element) {
        this.element = element;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "MakeFreeLayoutChildInvisible";
    }

    @NotNull
    @Override
    public String getText() {
        return "Make this free layout child invisible.";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        FileHelper.runWriteAction(()->{
            Property propertyWidth = element.addProperty();
            propertyWidth.getName().setValue("width");
            propertyWidth.getValue().setValue("1");

            Property propertyHeight = element.addProperty();
            propertyHeight.getName().setValue("height");
            propertyHeight.getValue().setValue("1");
        }, project, file.getVirtualFile());
    }
}
