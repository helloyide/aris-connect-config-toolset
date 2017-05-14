package com.piapox.idea.acct.autoReload.settings;

import static com.google.common.base.Strings.isNullOrEmpty;

public class AutoReloadSettings {
    private String ABSModuleName;
    private String COPModuleName;
    private String ABSTargetPath;
    private String COPTargetPath;
    private String liveLoadTriggerFile;

    private String username;
    private String password;
    private String urlLogin;
    private String urlABSReload;
    private String urlCOPReload;

    AutoReloadSettings(){

    }

    AutoReloadSettings(String ABSModuleName,
                       String COPModuleName,
                       String ABSTargetPath,
                       String COPTargetPath,
                       String liveLoadTriggerFile,
                       String username,
                       String password,
                       String urlABSReload,
                       String urlCOPReload,
                       String urlLogin
                       ) {
        this.ABSModuleName = ABSModuleName;
        this.COPModuleName = COPModuleName;
        this.ABSTargetPath = ABSTargetPath;
        this.COPTargetPath = COPTargetPath;
        this.liveLoadTriggerFile = liveLoadTriggerFile;
        this.username = username;
        this.password = password;
        this.urlABSReload = urlABSReload;
        this.urlCOPReload = urlCOPReload;
        this.urlLogin = urlLogin;
    }

    public boolean hasMinimalWorkingSettings() {
        return !isNullOrEmpty(username)
                && !isNullOrEmpty(password)
                && !isNullOrEmpty(urlABSReload)
                && !isNullOrEmpty(urlCOPReload)
                && !isNullOrEmpty(urlLogin);
    }

    public String getABSModuleName() {
        return ABSModuleName;
    }

    public String getCOPModuleName() {
        return COPModuleName;
    }

    public String getABSTargetPath() {
        return ABSTargetPath;
    }

    public String getCOPTargetPath() {
        return COPTargetPath;
    }

    public String getLiveLoadTriggerFile() {
        return liveLoadTriggerFile;
    }

    public void setABSModuleName(String ABSModuleName) {
        this.ABSModuleName = ABSModuleName;
    }

    public void setCOPModuleName(String COPModuleName) {
        this.COPModuleName = COPModuleName;
    }

    public void setABSTargetPath(String ABSTargetPath) {
        this.ABSTargetPath = ABSTargetPath;
    }

    public void setCOPTargetPath(String COPTargetPath) {
        this.COPTargetPath = COPTargetPath;
    }

    public void setLiveLoadTriggerFile(String liveLoadTriggerFile) {
        this.liveLoadTriggerFile = liveLoadTriggerFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrlLogin() {
        return urlLogin;
    }

    public void setUrlLogin(String urlLogin) {
        this.urlLogin = urlLogin;
    }

    public String getUrlABSReload() {
        return urlABSReload;
    }

    public void setUrlABSReload(String urlABSReload) {
        this.urlABSReload = urlABSReload;
    }

    public String getUrlCOPReload() {
        return urlCOPReload;
    }

    public void setUrlCOPReload(String urlCOPReload) {
        this.urlCOPReload = urlCOPReload;
    }

}
