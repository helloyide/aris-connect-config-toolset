package com.piapox.idea.acct.view.annotator.fix;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PsiNavigateUtil;
import com.piapox.idea.acct.util.FileHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class AddInstanceConfigFile extends BaseIntentionAction {

    private Project project;
    private String configSetName;
    private String componentType;
    private String instanceId;
    private VirtualFile defaultInstanceConfigFile;

    public AddInstanceConfigFile(Project project, String configSetName, String componentType, String instanceId, VirtualFile defaultInstanceConfigFile) {
        this.project = project;
        this.configSetName = configSetName;
        this.componentType = componentType;
        this.instanceId = instanceId;
        this.defaultInstanceConfigFile = defaultInstanceConfigFile;
    }

    @Nls
    @NotNull
    @Override
    public String getFamilyName() {
        return "AddInstanceConfigFile";
    }

    @NotNull
    @Override
    public String getText() {
        return "Create an new instance configuration based on the default configuration.";
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        FileHelper.runWriteAction(()->{
            VirtualFile parent = defaultInstanceConfigFile.getParent();
            String newInstanceConfigFileName = instanceId + ".xml";
            try {
                defaultInstanceConfigFile.copy(AddInstanceConfigFile.this, parent, newInstanceConfigFileName);
                VirtualFile newInstanceConfigFile = parent.findChild(newInstanceConfigFileName);
                if (newInstanceConfigFile != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(newInstanceConfigFile);
                    PsiNavigateUtil.navigate(psiFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, project, file.getVirtualFile());
    }
}
