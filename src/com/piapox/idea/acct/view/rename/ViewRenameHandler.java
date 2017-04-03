package com.piapox.idea.acct.view.rename;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.piapox.idea.acct.util.FileHelper;
import com.piapox.idea.acct.view.element.*;
import com.piapox.idea.acct.view.util.ViewHelper;
import groovy.transform.WithReadLock;
import io.netty.util.internal.StringUtil;
import org.jetbrains.annotations.CalledWithWriteLock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.piapox.idea.acct.util.FileHelper.collectInstanceConfigurations;
import static com.piapox.idea.acct.util.FileHelper.getConfigSetName;
import static com.piapox.idea.acct.view.util.ViewHelper.findViewConfigElementByInstanceId;

public class ViewRenameHandler implements RenameHandler {
    @Override
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
        if (!ViewHelper.isViewConfiguration(psiFile)) return false;
        return getInstanceIdOnCaret(dataContext) != null;
    }

    @Override
    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        invoke(project, PsiElement.EMPTY_ARRAY, dataContext);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        String oldInstanceId = getInstanceIdOnCaret(dataContext);
        if (oldInstanceId == null) return;

        // this must be done before UI dialog shows up
        // otherwise we will get an error: "cannot share data context between Swing events"
        XmlFile xmlFile = (XmlFile) CommonDataKeys.PSI_FILE.getData(dataContext);
        if (xmlFile == null) return;
        Layout rootLayout = ViewHelper.getRootLayout(xmlFile);
        View rootView = ViewHelper.getRootView(xmlFile);
        if (rootLayout == null || rootView == null) return;

        String newInstanceId = Messages.showInputDialog(
                project,
                "Please input the new instance id",
                "Rename Instance",
                Messages.getQuestionIcon(),
                oldInstanceId,
                null);

        if (StringUtil.isNullOrEmpty(newInstanceId)) return;

        // find all related elements and rename their instances attributes
        FileHelper.runWriteCommand(()->{
            renameInstanceConfigFile(project, oldInstanceId, xmlFile, rootView, newInstanceId);
            renameViewConfigElementInstanceId(rootLayout, oldInstanceId, newInstanceId);
            renameWireInstanceIds(rootView.getWires(), oldInstanceId, newInstanceId);
        }, project, xmlFile.getVirtualFile());

    }

    /**
     * This function should be called before changing the xml content, so the oldInstanceId is still valid.
     */
    @WithReadLock
    private void renameInstanceConfigFile(@NotNull Project project, String oldInstanceId, XmlFile xmlFile, View rootView, String newInstanceId) {
        ViewConfigElement viewConfigElement = findViewConfigElementByInstanceId(rootView, oldInstanceId);
        if (viewConfigElement instanceof Component || viewConfigElement instanceof LayoutComponent) {
            String componentType;
            if (viewConfigElement instanceof Component) {
                componentType = ((Component) viewConfigElement).getType().getStringValue();
            } else {
                componentType = ((LayoutComponent) viewConfigElement).getType().getStringValue();
            }
            String configSetName = getConfigSetName(xmlFile.getVirtualFile());
            List<VirtualFile> instanceConfigFiles = new ArrayList<>();
            collectInstanceConfigurations(project, configSetName, componentType, oldInstanceId, instanceConfigFiles);

            // we only consider the equals case
            Optional<VirtualFile> instanceConfigFile = instanceConfigFiles.stream()
                    .filter(virtualFile -> virtualFile.getName().equals(oldInstanceId + ".xml")).findFirst();
            if (instanceConfigFile.isPresent()) {
                try {
                    instanceConfigFile.get().rename(ViewRenameHandler.this, newInstanceId + ".xml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @WithReadLock
    private void renameWireInstanceIds(Wires wires, String oldInstanceId, String newInstanceId) {
        wires.getWires().forEach(wire -> {
            tryUpdateWireInstanceIds(wire, oldInstanceId, newInstanceId);
        });
    }

    @WithReadLock
    private void renameViewConfigElementInstanceId(ViewConfigElement root, final String oldInstanceId, final String newInstanceId) {
        tryUpdateInstanceId(root, oldInstanceId, newInstanceId);

        root.acceptChildren(new DomElementVisitor() {
            @Override
            public void visitDomElement(DomElement element) {
                // do nothing
            }

            public void visitComponent(Component component) {
                tryUpdateInstanceId(component, oldInstanceId, newInstanceId);
            }

            public void visitView(View view) {
                tryUpdateInstanceId(view, oldInstanceId, newInstanceId);
            }

            public void visitLayout(Layout layout) {
                renameViewConfigElementInstanceId(layout, oldInstanceId, newInstanceId);
            }

            public void visitLayoutComponent(LayoutComponent layoutComponent) {
                renameViewConfigElementInstanceId(layoutComponent, oldInstanceId, newInstanceId);
            }
        });
    }

    @CalledWithWriteLock
    private void tryUpdateInstanceId(ViewConfigElement viewConfigElement, String oldInstanceId, String newInstanceId) {
        if (oldInstanceId.equals(viewConfigElement.getInstance().getStringValue())) {
            viewConfigElement.getInstance().setValue(newInstanceId);
        }
    }

    @CalledWithWriteLock
    private void tryUpdateWireInstanceIds(Wire wire, String oldInstanceId, String newInstanceId) {
        if (oldInstanceId.equals(wire.getSourceInstance().getStringValue())) {
            wire.getSourceInstance().setValue(newInstanceId);
        }
        if (oldInstanceId.equals(wire.getTargetInstance().getStringValue())) {
            wire.getTargetInstance().setValue(newInstanceId);
        }
    }

    @Nullable
    private String getInstanceIdOnCaret(DataContext context) {
        Editor editor = CommonDataKeys.EDITOR.getData(context);
        PsiFile file = CommonDataKeys.PSI_FILE.getData(context);
        if (editor == null || file == null) return null;

        PsiElement target = file.findElementAt(editor.getCaretModel().getOffset());
        if (target == null) return null;

        if (target instanceof XmlTokenImpl) {
            XmlTokenImpl xmlToken = (XmlTokenImpl) target;
            if (xmlToken.getTokenType().equals(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)) {
                PsiElement attributeValue = xmlToken.getParent();
                if (attributeValue != null) {
                    PsiElement attribute = attributeValue.getParent();
                    if (attribute != null && attribute instanceof XmlAttribute) {
                        XmlAttribute xmlAttribute = (XmlAttribute) attribute;
                        if (isInstanceAttribute(xmlAttribute)) {
                            return xmlAttribute.getValue();
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isInstanceAttribute(XmlAttribute attribute) {
        String attributeName = attribute.getName();
        return attributeName.equals("sourceInstance") || attributeName.equals("targetInstance") || attributeName.equals("instance");
    }

}
