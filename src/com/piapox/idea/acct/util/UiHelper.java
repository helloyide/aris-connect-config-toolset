package com.piapox.idea.acct.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class UiHelper {

    public static void alert(Object message) {
        String msg;
        if (message == null) {
            msg = "null";
        } else {
            msg = message.toString();
        }
        Notifications.Bus.notify(new Notification("", "", msg, NotificationType.INFORMATION));
    }

}
