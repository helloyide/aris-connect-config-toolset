package com.piapox.idea.acct.ui.autoReloadSettings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.awt.event.*;

public class AutoReloadSettingsDialog extends JDialog {
    private final Project project = null;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtABSModuleName;
    private JTextField txtCOPModuleName;
    private JTextField txtABSTargetPath;
    private JTextField txtCOPTargetPath;
    private JTextField txtLiveLoadTriggerFile;
    private JButton btnABSTargetPath;
    private JButton btnCOPTargetPath;
    private JButton btnLiveLoadTriggerFile;

    public AutoReloadSettingsDialog() {
//        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if (project != null) {
            btnLiveLoadTriggerFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FileChooserDescriptor fileDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor((String) null);
                    VirtualFile selectedFile = FileChooser.chooseFile(fileDescriptor, project, null);
                    if (selectedFile != null) {
                        txtLiveLoadTriggerFile.setText(selectedFile.getCanonicalPath());
                    }
                }
            });
        }
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
