package com.piapox.idea.acct.view.lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.piapox.idea.acct.view.element.Component;
import com.piapox.idea.acct.view.element.LayoutComponent;
import com.piapox.idea.acct.view.element.ViewConfigElement;
import icons.Icons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.piapox.idea.acct.util.FileHelper.*;

class ComponentLineMarkerVisitor implements DomElementVisitor {

    private Collection<? super RelatedItemLineMarkerInfo> result;

    @Override
    public void visitDomElement(DomElement element) {
        // Do nothing
    }

    public void visitComponent(Component component) {
        addWireMarker(component);
    }

    public void visitLayoutComponent(LayoutComponent layoutComponent) {
        addWireMarker(layoutComponent);
    }

    private void addWireMarker(ViewConfigElement element) {
        String componentType = element.getXmlTag().getAttributeValue("type");
        if (componentType == null) return;

        Project project = element.getXmlTag().getProject();
        String configSetName = getConfigSetName(element.getXmlTag().getContainingFile().getVirtualFile());
        String instanceId = element.getInstance().getStringValue();

        // find instance configuration with the same name or end with "-instanceId" or "*_default"
        // in current config set on both runnables

        List<VirtualFile> instanceConfigFiles = new ArrayList<>();
        collectInstanceConfigurations(project, configSetName, componentType, instanceId, instanceConfigFiles);

        if (!instanceConfigFiles.isEmpty()) {

            List<XmlFile> targets = instanceConfigFiles.stream().map(file -> {
                XmlFile xmlFile = toXmlFile(project, file);
                return xmlFile != null ? xmlFile : null;
            }).collect(Collectors.toList());

            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(Icons.COMPONENT)
                    .setTooltipText("Navigate to instance configurations.")
                    .setPopupTitle("Related Instance Configuration")
                    .setTargets(targets);

            result.add(builder.createLineMarkerInfo(element.getXmlTag()));
        }

    }

    void setResult(Collection<? super RelatedItemLineMarkerInfo> result) {
        this.result = result;
    }
}
