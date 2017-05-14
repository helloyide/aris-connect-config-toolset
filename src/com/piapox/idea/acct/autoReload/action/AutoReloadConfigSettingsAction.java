package com.piapox.idea.acct.autoReload.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.piapox.idea.acct.autoReload.settings.AutoReloadSettingsDialog;


public class AutoReloadConfigSettingsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        AutoReloadSettingsDialog dialog = new AutoReloadSettingsDialog(project);
        dialog.setVisible(true);
    }
}
