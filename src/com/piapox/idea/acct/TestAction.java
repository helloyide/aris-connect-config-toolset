package com.piapox.idea.acct;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.LocalFilePath;
import com.intellij.openapi.vcs.changes.*;
import com.intellij.openapi.vcs.changes.patch.CreatePatchCommitExecutor;
import com.intellij.openapi.vcs.changes.ui.CommitChangeListDialog;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.openapi.vcs.FileStatus.MODIFIED;


public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
//        foo(project);
        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("xml");
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        System.out.println(virtualFile);
    }

    private void foo(final Project project) {
        List<Change> changes = new ArrayList<>();
        Change change = null;
        change = new Change(
                new ContentRevision() {
                    @Override
                    public String getContent() {
                        return "getContent1";
                    }

                    @NotNull
                    @Override
                    public FilePath getFile() {
                        return new LocalFilePath("C:/SoftwareAG/ARIS10.0/server/bin/work/work_copernicus_s/base/webapps/ROOT/WEB-INF/config/classic/views/profile.xml", false);
                    }

                    @NotNull
                    @Override
                    public VcsRevisionNumber getRevisionNumber() {
                        return VcsRevisionNumber.NULL;
                    }
                },
                new ContentRevision() {
                    @Override
                    public String getContent() {
                        return "getContent2";
                    }

                    @NotNull
                    @Override
                    public FilePath getFile() {
                        return new LocalFilePath("C:/SoftwareAG/ARIS10.0/server/bin/work/work_copernicus_s/base/webapps/ROOT/WEB-INF/config/classic/views/home.xml", false);
                    }

                    @NotNull
                    @Override
                    public VcsRevisionNumber getRevisionNumber() {
                        return VcsRevisionNumber.NULL;
                    }
                },
                MODIFIED);
        changes.add(change);
        CommitChangeListDialog.commitChanges(
                project,
                changes,
                new LocalChangeList() {
                    @Override
                    public Collection<Change> getChanges() {
                        return changes;
                    }

                    @NotNull
                    @Override
                    public String getName() {
                        return "LocalChangeList";
                    }

                    @Override
                    public void setName(@NotNull String name) {

                    }

                    @Nullable
                    @Override
                    public String getComment() {
                        return "getComment";
                    }

                    @Override
                    public void setComment(@Nullable String comment) {

                    }

                    @Override
                    public boolean isDefault() {
                        return false;
                    }

                    @Override
                    public boolean isReadOnly() {
                        return false;
                    }

                    @Override
                    public void setReadOnly(boolean isReadOnly) {

                    }

                    @Nullable
                    @Override
                    public Object getData() {
                        return null;
                    }

                    @Override
                    public LocalChangeList copy() {
                        return this;
                    }
                },
        new CommitExecutor() {
                    @Nls
                    @Override
                    public String getActionText() {
                        return "getActionText";
                    }

                    @NotNull
                    @Override
                    public CommitSession createCommitSession() {
                        CreatePatchCommitExecutor createPatchCommitExecutor = new CreatePatchCommitExecutor(project, null);
                        return createPatchCommitExecutor.createCommitSession();
                    }
                },
                "comment"
        );
    }


}
