package com.piapox.idea.acct.autoReload.settings;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

public class AutoReloadSettingsDAO {

    private static final String KEY_ABSModuleName = "AUTO_RELOAD_SETTINGS_ABS_MODULE_NAME";
    private static final String KEY_COPModuleName = "AUTO_RELOAD_SETTINGS_COP_MODULE_NAME";
    private static final String KEY_ABSTargetPath = "AUTO_RELOAD_SETTINGS_ABS_TARGET_PATH";
    private static final String KEY_COPTargetPath = "AUTO_RELOAD_SETTINGS_COP_TARGET_PATH";
    private static final String KEY_LiveLoadTriggerFile = "AUTO_RELOAD_SETTINGS_LIVELOAD_TRIGGER_FILE";
    private static final String KEY_Username = "AUTO_RELOAD_SETTINGS_USERNAME";
    private static final String KEY_Password = "AUTO_RELOAD_SETTINGS_PASSWORD";
    private static final String KEY_URL_ABS_RELOAD = "AUTO_RELOAD_SETTINGS_URL_ABS_RELOAD";
    private static final String KEY_URL_COP_RELOAD = "AUTO_RELOAD_SETTINGS_URL_COP_RELOAD";
    private static final String KEY_URL_LOGIN = "AUTO_RELOAD_SETTINGS_URL_LOGIN";

    public static AutoReloadSettings loadSettings(Project project) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        return new AutoReloadSettings(
                propertiesComponent.getValue(KEY_ABSModuleName),
                propertiesComponent.getValue(KEY_COPModuleName),
                propertiesComponent.getValue(KEY_ABSTargetPath),
                propertiesComponent.getValue(KEY_COPTargetPath),
                propertiesComponent.getValue(KEY_LiveLoadTriggerFile),
                propertiesComponent.getValue(KEY_Username),
                propertiesComponent.getValue(KEY_Password),
                propertiesComponent.getValue(KEY_URL_ABS_RELOAD),
                propertiesComponent.getValue(KEY_URL_COP_RELOAD),
                propertiesComponent.getValue(KEY_URL_LOGIN)
        );
    }

    static void saveSettings(Project project, AutoReloadSettings settings) {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
        propertiesComponent.setValue(KEY_ABSModuleName, settings.getABSModuleName());
        propertiesComponent.setValue(KEY_COPModuleName, settings.getCOPModuleName());
        propertiesComponent.setValue(KEY_ABSTargetPath, settings.getABSTargetPath());
        propertiesComponent.setValue(KEY_COPTargetPath, settings.getCOPTargetPath());
        propertiesComponent.setValue(KEY_LiveLoadTriggerFile, settings.getLiveLoadTriggerFile());
        propertiesComponent.setValue(KEY_Username, settings.getUsername());
        propertiesComponent.setValue(KEY_Password, settings.getPassword());
        propertiesComponent.setValue(KEY_URL_ABS_RELOAD, settings.getUrlABSReload());
        propertiesComponent.setValue(KEY_URL_COP_RELOAD, settings.getUrlCOPReload());
        propertiesComponent.setValue(KEY_URL_LOGIN, settings.getUrlLogin());
    }
}
