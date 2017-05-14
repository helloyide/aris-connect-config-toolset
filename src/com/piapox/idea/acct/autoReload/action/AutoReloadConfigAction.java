package com.piapox.idea.acct.autoReload.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.piapox.idea.acct.autoReload.ConfigBulkFileListener;
import com.piapox.idea.acct.autoReload.settings.AutoReloadSettingsDialog;
import icons.Icons;

import static com.intellij.openapi.ui.Messages.showMessageDialog;
import static com.intellij.openapi.vfs.VirtualFileManager.VFS_CHANGES;
import static com.piapox.idea.acct.autoReload.settings.AutoReloadSettingsDAO.loadSettings;


public class AutoReloadConfigAction extends AnAction {

    private boolean reloadEnabled = false;
    private MessageBusConnection busConnection;

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) return;

        if (!loadSettings(project).hasMinimalWorkingSettings()) {
            showMessageDialog("Please check the settings first, then click this icon again to activate it.", "Incorrect Settings", null);
            AutoReloadSettingsDialog dialog = new AutoReloadSettingsDialog(project);
            dialog.setVisible(true);
            return;
        }

        reloadEnabled = !reloadEnabled;
        if (reloadEnabled) {
            e.getPresentation().setIcon(Icons.RELOAD_ENABLED);
            ConfigBulkFileListener bulkFileListener = new ConfigBulkFileListener(project);
            if (busConnection == null) {
                busConnection = ApplicationManager.getApplication().getMessageBus().connect(project);
                busConnection.subscribe(VFS_CHANGES, bulkFileListener);
            }
        } else {
            e.getPresentation().setIcon(Icons.RELOAD_DISABLED);
            // unsubscribe
            if (busConnection != null) {
                busConnection.disconnect();
                busConnection = null;
            }
        }

    }




}
