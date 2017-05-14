package com.piapox.idea.acct.autoReload.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.piapox.idea.acct.projectType.CodeLineConfigProject;
import com.piapox.idea.acct.projectType.ConfigProject;
import com.piapox.idea.acct.projectType.ConfigProjectFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.google.common.base.Strings.isNullOrEmpty;

public class AutoReloadSettingsDialog extends JDialog {

    private static final String DEFAULT_ABS_TARGET_PATH = "U:\\ALL\\dev\\s\\cop\\apps\\aris\\aris\\server-complete\\target\\y-aris-server-complete-99.0.0.0-SNAPSHOT";
    private static final String DEFAULT_COP_TARGET_PATH = "U:\\ALL\\dev\\s\\cop\\base\\copernicus\\portal-server\\target\\portal-server-99.0.0.0-SNAPSHOT";
    private static final String DEFAULT_USERNAME = "system";
    private static final String DEFAULT_PASSWORD = "manager";
    private static final String DEFAULT_URL_ABS_RELOAD = "http://pcy9999901.eur.ad.sag/abs/copernicus/default/service/reconfigure";
    private static final String DEFAULT_URL_COP_RELOAD = "http://pcy9999901.eur.ad.sag/copernicus/default/service/reconfigure";
    private static final String DEFAULT_URL_LOGIN = "http://pcy9999901.eur.ad.sag/copernicus/default/service/login";
    private static final String DEFAULT_ABS_MODULE_NAME = "cop-pub-config";
    private static final String DEFAULT_COP_MODULE_NAME = "portal-config";

    private final Project project;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtABSTargetPath;
    private JTextField txtCOPTargetPath;
    private JTextField txtLiveLoadTriggerFile;
    private JButton btnABSTargetPath;
    private JButton btnCOPTargetPath;
    private JButton btnLiveLoadTriggerFile;
    private JComboBox<String> cbABSModuleName;
    private JComboBox<String> cbCOPModuleName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtURLLogin;
    private JTextField txtURLABSReload;
    private JTextField txtURLCOPReload;
    private JCheckBox cbShowPassword;

    public AutoReloadSettingsDialog(Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        initComboBoxes();
        setDefaultValues();
        setCurrentValues();
        setupInputValidators();
        initListeners(project);
        setupDialogSizeAndPosition();
    }

    private void setupDialogSizeAndPosition() {
        setTitle("AutoReload Settings");
        setSize(780, 500);
        // put the dialog in screen center (also works with multi screen)
        setLocationRelativeTo(KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow());
    }

    private void initComboBoxes() {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            String name = module.getName();
            cbABSModuleName.addItem(name);
            cbCOPModuleName.addItem(name);
        }
    }

    private void setCurrentValues() {
        AutoReloadSettings autoReloadSettings = AutoReloadSettingsDAO.loadSettings(project);

        String absModuleName = autoReloadSettings.getABSModuleName();
        String copModuleName = autoReloadSettings.getCOPModuleName();
        String absTargetPath = autoReloadSettings.getABSTargetPath();
        String copTargetPath = autoReloadSettings.getCOPTargetPath();
        String liveLoadTriggerFile = autoReloadSettings.getLiveLoadTriggerFile();
        String username = autoReloadSettings.getUsername();
        String password = autoReloadSettings.getPassword();
        String urlABSReload = autoReloadSettings.getUrlABSReload();
        String urlCOPReload = autoReloadSettings.getUrlCOPReload();
        String urlLogin = autoReloadSettings.getUrlLogin();

        if (!isNullOrEmpty(absModuleName)) {
            cbABSModuleName.setSelectedItem(absModuleName);
        }
        if (!isNullOrEmpty(copModuleName)) {
            cbCOPModuleName.setSelectedItem(copModuleName);
        }
        if (!isNullOrEmpty(absTargetPath)) {
            txtABSTargetPath.setText(absTargetPath);
        }
        if (!isNullOrEmpty(copTargetPath)) {
            txtCOPTargetPath.setText(copTargetPath);
        }
        if (!isNullOrEmpty(liveLoadTriggerFile)) {
            txtLiveLoadTriggerFile.setText(liveLoadTriggerFile);
        }
        if (!isNullOrEmpty(username)) {
            txtUsername.setText(username);
        }
        if (!isNullOrEmpty(password)) {
            txtPassword.setText(password);
        }
        if (!isNullOrEmpty(urlABSReload)) {
            txtURLABSReload.setText(urlABSReload);
        }
        if (!isNullOrEmpty(urlCOPReload)) {
            txtURLCOPReload.setText(urlCOPReload);
        }
        if (!isNullOrEmpty(urlLogin)) {
            txtURLLogin.setText(urlLogin);
        }
    }

    private void setDefaultValues() {
        ConfigProject configProject = ConfigProjectFactory.getConfigProject(project);
        if (configProject instanceof CodeLineConfigProject) {
            cbABSModuleName.setSelectedItem(DEFAULT_ABS_MODULE_NAME);
            cbCOPModuleName.setSelectedItem(DEFAULT_COP_MODULE_NAME);
            txtABSTargetPath.setText(DEFAULT_ABS_TARGET_PATH);
            txtCOPTargetPath.setText(DEFAULT_COP_TARGET_PATH);
        }

        txtUsername.setText(DEFAULT_USERNAME);
        txtPassword.setText(DEFAULT_PASSWORD);
        txtURLABSReload.setText(DEFAULT_URL_ABS_RELOAD);
        txtURLCOPReload.setText(DEFAULT_URL_COP_RELOAD);
        txtURLLogin.setText(DEFAULT_URL_LOGIN);
    }

    private void setupInputValidators() {

        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();

        txtABSTargetPath.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = txtABSTargetPath.getText();
                return isNullOrEmpty(text) || localFileSystem.findFileByPath(text) != null;
            }
        });

        txtCOPTargetPath.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = txtCOPTargetPath.getText();
                return isNullOrEmpty(text) || localFileSystem.findFileByPath(text) != null;
            }
        });

        txtLiveLoadTriggerFile.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                String text = txtLiveLoadTriggerFile.getText();
                return isNullOrEmpty(text) || (localFileSystem.findFileByPath(text) != null);
            }
        });

        txtUsername.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return !isNullOrEmpty(txtUsername.getText());
            }
        });

        txtPassword.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return !isNullOrEmpty(new String(txtPassword.getPassword()));
            }
        });

        txtURLABSReload.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return !isNullOrEmpty(txtURLABSReload.getText());
            }
        });

        txtURLCOPReload.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return !isNullOrEmpty(txtURLCOPReload.getText());
            }
        });

        txtURLLogin.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                return !isNullOrEmpty(txtURLLogin.getText());
            }
        });

    }

    private void initListeners(final Project project) {
        FileChooserDescriptor fileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();
        FileChooserDescriptor folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();

        btnLiveLoadTriggerFile.addActionListener(e -> {
            VirtualFile selected = FileChooser.chooseFile(fileDescriptor, project, null);
            if (selected != null) {
                txtLiveLoadTriggerFile.setText(selected.getCanonicalPath());
            }
        });
        btnCOPTargetPath.addActionListener(e -> {
            VirtualFile selected = FileChooser.chooseFile(folderDescriptor, project, null);
            if (selected != null) {
                txtCOPTargetPath.setText(selected.getCanonicalPath());
            }
        });
        btnABSTargetPath.addActionListener(e -> {
            VirtualFile selected = FileChooser.chooseFile(folderDescriptor, project, null);
            if (selected != null) {
                txtABSTargetPath.setText(selected.getCanonicalPath());
            }
        });

        cbShowPassword.addActionListener(e -> {
            if (cbShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('*');
            }
        });
    }

    private void onOK() {
        if (areInputsValid()) {
            saveSettings();
            dispose();
        } else {
            Messages.showMessageDialog("There are invalid value(s) found.", "Invalid Settings", null);
        }
    }

    private boolean areInputsValid() {
        return cbABSModuleName.isValid()
                && cbCOPModuleName.isValid()
                && txtABSTargetPath.isValid()
                && txtCOPTargetPath.isValid()
                && txtLiveLoadTriggerFile.isValid()
                && txtUsername.isValid()
                && txtPassword.isValid()
                && txtURLABSReload.isValid()
                && txtURLCOPReload.isValid()
                && txtURLLogin.isValid()
                ;
    }

    private void saveSettings() {
        AutoReloadSettings settings = new AutoReloadSettings();

        String txtABSModuleNameText = (String) cbABSModuleName.getSelectedItem();
        String txtCOPModuleNameText = (String) cbCOPModuleName.getSelectedItem();
        String txtABSTargetPathText = txtABSTargetPath.getText();
        String txtCOPTargetPathText = txtCOPTargetPath.getText();
        String txtLiveLoadTriggerFileText = txtLiveLoadTriggerFile.getText();
        String txtUsernameText = txtUsername.getText();
        String txtPasswordText = new String(txtPassword.getPassword());
        String txtURLABSReloadText = txtURLABSReload.getText();
        String txtURLCOPReloadText = txtURLCOPReload.getText();
        String txtURLLoginText = txtURLLogin.getText();

        if (!isNullOrEmpty(txtABSModuleNameText)) {
            settings.setABSModuleName(txtABSModuleNameText);
        }
        if (!isNullOrEmpty(txtCOPModuleNameText)) {
            settings.setCOPModuleName(txtCOPModuleNameText);
        }
        if (!isNullOrEmpty(txtABSTargetPathText)) {
            settings.setABSTargetPath(txtABSTargetPathText);
        }
        if (!isNullOrEmpty(txtCOPTargetPathText)) {
            settings.setCOPTargetPath(txtCOPTargetPathText);
        }
        if (!isNullOrEmpty(txtLiveLoadTriggerFileText)) {
            settings.setLiveLoadTriggerFile(txtLiveLoadTriggerFileText);
        }
        if (!isNullOrEmpty(txtUsernameText)) {
            settings.setUsername(txtUsernameText);
        }
        if (!isNullOrEmpty(txtPasswordText)) {
            settings.setPassword(txtPasswordText);
        }
        if (!isNullOrEmpty(txtURLABSReloadText)) {
            settings.setUrlABSReload(txtURLABSReloadText);
        }
        if (!isNullOrEmpty(txtURLCOPReloadText)) {
            settings.setUrlCOPReload(txtURLCOPReloadText);
        }
        if (!isNullOrEmpty(txtURLLoginText)) {
            settings.setUrlLogin(txtURLLoginText);
        }

        AutoReloadSettingsDAO.saveSettings(project, settings);
    }
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AutoReloadSettingsDialog dialog = new AutoReloadSettingsDialog(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // place custom component creation code here
    }

}
