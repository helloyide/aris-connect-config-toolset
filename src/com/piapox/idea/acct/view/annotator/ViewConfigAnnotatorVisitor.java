package com.piapox.idea.acct.view.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.intellij.util.xml.GenericAttributeValue;
import com.piapox.idea.acct.util.FileHelper;
import com.piapox.idea.acct.util.XmlHelper;
import com.piapox.idea.acct.view.annotator.fix.*;
import com.piapox.idea.acct.view.element.*;
import com.piapox.idea.acct.view.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static com.piapox.idea.acct.util.FileHelper.collectInstanceConfigurations;
import static com.piapox.idea.acct.util.FileHelper.getConfigSetName;
import static com.piapox.idea.acct.view.util.ViewHelper.findViewConfigElementByInstanceId;
import static com.piapox.idea.acct.view.util.ViewHelper.isRootView;

class ViewConfigAnnotatorVisitor implements DomElementVisitor {

    private AnnotationHolder holder;
    private View rootView;

    @Override
    public void visitDomElement(DomElement element) {
        // Do nothing
    }

    public void visitWire(Wire wire) {
        if (!isWireValid(wire)) {
            Annotation annotation = holder.createWarningAnnotation(
                    wire.getXmlTag(),
                    "The source or target of the wire is invalid.");
            annotation.registerFix(new RemoveInvalidWire(wire));
        }
    }

    /**
     * Check if the source and target of the wire are valid
     * empty source/target instance is considered as valid
     */
    private boolean isWireValid(Wire wire) {
        String sourceInstance = wire.getSourceInstance().getStringValue();
        String sourceGroup = wire.getSourceGroup().getStringValue();
        String targetInstance = wire.getTargetInstance().getStringValue();
        String targetGroup = wire.getTargetGroup().getStringValue();

        if (sourceInstance != null) {
            ViewConfigElement found = findViewConfigElementByInstanceId(rootView, sourceInstance);
            if (found == null && !sourceInstance.isEmpty()) return false;
        } else if (sourceGroup != null) {
            // TODO:
            return true;
        } else {
            return false;
        }

        if (targetInstance != null) {
            ViewConfigElement found = findViewConfigElementByInstanceId(rootView, targetInstance);
            if (found == null && !targetInstance.isEmpty()) return false;
        } else if (targetGroup != null) {
            // TODO:
            return true;
        } else {
            return false;
        }

        return true;
    }

    public void visitLayout(Layout layout) {
        checkIfAllFreeLayoutChildrenHaveSize(layout);
    }

    public void visitComponent(Component component) {
        checkIfUsingDefaultInstanceConfig(component, component.getType().getStringValue());
    }

    public void visitView(View view) {
        checkIfViewInstanceAndFilenameMatched(view);
    }

    public void visitLayoutComponent(LayoutComponent layoutComponent) {
        checkIfUsingDefaultInstanceConfig(layoutComponent, layoutComponent.getType().getStringValue());
    }

    private void checkIfUsingDefaultInstanceConfig(ViewConfigElement viewConfigElement, String componentType) {
        Project project = rootView.getXmlTag().getProject();
        String configSetName = getConfigSetName(rootView.getXmlTag().getContainingFile().getVirtualFile());
        String instanceId = viewConfigElement.getInstance().getStringValue();
        List<VirtualFile> instanceConfigFiles = new ArrayList<>();
        boolean onlyDefault = collectInstanceConfigurations(project, configSetName, componentType, instanceId, instanceConfigFiles);
        if (onlyDefault) {
            VirtualFile defaultInstanceConfigFile = instanceConfigFiles.get(0);
            Annotation annotation = holder.createWeakWarningAnnotation(
                    XmlHelper.getOpenTagTextRange(viewConfigElement.getXmlTag()),
                    "Using default instance configuration should be avoid if possible.");
            annotation.registerFix(new AddInstanceConfigFile(project, configSetName, componentType, instanceId, defaultInstanceConfigFile));
        }
    }

    private void checkIfViewInstanceAndFilenameMatched(View view) {
        if (!isRootView(view)) return;

        GenericAttributeValue<String> instanceIdAttributeValue = view.getInstance();
        String instanceId = instanceIdAttributeValue.getStringValue();
        if (instanceId == null) return;

        XmlElement xmlElement = view.getXmlElement();
        if (xmlElement != null) {
            PsiFile file = xmlElement.getContainingFile();
            String fileNameWithoutExtension = FileHelper.getFileNameWithoutExtension(file.getName());
            if (!fileNameWithoutExtension.equals(instanceId)) {
                //noinspection ConstantConditions
                Annotation annotation = holder.createWarningAnnotation(
                        view.getXmlTag().getAttribute("instance").getTextRange(),
                        "File name should be same as the view instance id.");
                annotation.registerFix(new RenameFileToViewInstanceId(instanceId));
                annotation.registerFix(new ChangeViewInstanceIdToFileName(fileNameWithoutExtension, instanceIdAttributeValue));
            }
        }
    }

    private void checkIfAllFreeLayoutChildrenHaveSize(Layout layout) {
        if (!"free".equals(ViewHelper.getLayoutType(layout))) return;

        List<ViewConfigElement> layoutChildren = new ArrayList<>();
        layoutChildren.addAll(layout.getComponents());
        layoutChildren.addAll(layout.getLayouts());
        layoutChildren.addAll(layout.getViews());
        layoutChildren.addAll(layout.getLayoutComponents());

        layoutChildren.forEach(child -> {
            boolean[] hasSize = new boolean[]{false};
            child.getProperties().forEach(property -> {
                String propertyValue = property.getName().getStringValue();
                if ("width".equals(propertyValue) || "height".equals(propertyValue)) {
                    hasSize[0] = true;
                }
            });
            if (!hasSize[0]) {
                //noinspection ConstantConditions
                Annotation annotation = holder.createWeakWarningAnnotation(
                        XmlHelper.getOpenTagTextRange(child.getXmlTag()),
                        "Free layout child without 'width' and 'height' properties will take full size of its parent, is that what you want?");
                annotation.registerFix(new MakeFreeLayoutChildInvisible(child));
                annotation.registerFix(new AddFreeLayoutChildZeroSizeProperties(child));
            }
        });
    }


    void setHolder(AnnotationHolder holder) {
        this.holder = holder;
    }

    void setRootView(View rootView) {
        this.rootView = rootView;
    }
}
