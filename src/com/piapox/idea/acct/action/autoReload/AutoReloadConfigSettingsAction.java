package com.piapox.idea.acct.action.autoReload;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.piapox.idea.acct.ui.autoReloadSettings.AutoReloadSettingsDialog;


public class AutoReloadConfigSettingsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        AutoReloadSettingsDialog diag = new AutoReloadSettingsDialog();

        diag.setTitle("AutoReload Settings");
        diag.setSize(500, 255);
        // put the dialog in screen center
        diag.setLocationRelativeTo(null);

        diag.setVisible(true);
    }
}
