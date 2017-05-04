package com.piapox.idea.acct.action.autoReload;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import icons.Icons;

import static com.intellij.openapi.vfs.VirtualFileManager.VFS_CHANGES;


public class AutoReloadConfigAction extends AnAction {

    private boolean reloadEnabled = false;
    private ConfigBulkFileListener bulkFileListener;
    private MessageBusConnection busConnection;

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project project = e.getProject();
        if (project == null) return;

        reloadEnabled = !reloadEnabled;
        if (reloadEnabled) {
            e.getPresentation().setIcon(Icons.RELOAD_ENABLED);
            if (bulkFileListener == null) {
                bulkFileListener = new ConfigBulkFileListener(project);
            }
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
