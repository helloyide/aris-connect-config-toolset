package com.piapox.idea.acct.view.lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.piapox.idea.acct.view.element.*;
import icons.Icons;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.piapox.idea.acct.view.util.ViewHelper.getRootView;
import static com.piapox.idea.acct.view.util.ViewHelper.toWire;

class WireLineMarkerVisitor implements DomElementVisitor {

    private static final int WIRE_SLOT_TEXT_MAX_WIDTH = 60;
    private Collection<? super RelatedItemLineMarkerInfo> result;

    @Override
    public void visitDomElement(DomElement element) {
        // Do nothing
    }

    public void visitLayout(Layout layout) {
        addWireMarker(layout);
    }

    public void visitComponent(Component component) {
        addWireMarker(component);
    }

    public void visitView(View view) {
        addWireMarker(view);
    }

    public void visitLayoutComponent(LayoutComponent layoutComponent) {
        addWireMarker(layoutComponent);
    }

    private void addWireMarker(ViewConfigElement element) {
        String instanceId = element.getInstance().getStringValue();
        if (instanceId == null) return;

        View rootView = getRootView((XmlFile) element.getXmlTag().getContainingFile());
        if (rootView != null) {

            List<PsiElement> relatedWires = rootView.getWires().getWires().stream()
                    .filter(wire -> instanceId.equals(wire.getSourceInstance().getStringValue()))
                    .map(DomElement::getXmlTag)
                    .collect(Collectors.toList());

            relatedWires.addAll(rootView.getWires().getWires().stream()
                    .filter(wire -> instanceId.equals(wire.getTargetInstance().getStringValue()))
                    .map(DomElement::getXmlTag)
                    .collect(Collectors.toList()));

            if (relatedWires.isEmpty()) return;

            NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder.create(Icons.WIRE)
                    .setCellRenderer(new DefaultPsiElementCellRenderer() {

                        @Override
                        public String getElementText(PsiElement element) {
                            Wire wire = toWire((XmlTag) element);
                            if (wire == null) return null;

                            String source;
                            String sourceInstance = wire.getSourceInstance().getStringValue();
                            if (sourceInstance == null) {
                                source = "[" + wire.getSourceGroup().getStringValue() + "]";
                            } else {
                                source = sourceInstance;
                            }

                            String target;
                            String targetInstance = wire.getTargetInstance().getStringValue();
                            if (targetInstance == null) {
                                target = "[" + wire.getTargetGroup().getStringValue() + "]";
                            } else {
                                target = targetInstance;
                            }

                            String sourceSlot = StringUtils.abbreviate(wire.getSourceSlot().getStringValue(), WIRE_SLOT_TEXT_MAX_WIDTH);
                            String targetSlot = StringUtils.abbreviate(wire.getTargetSlot().getStringValue(), WIRE_SLOT_TEXT_MAX_WIDTH);

                            return source + ":" + sourceSlot + " -> " + target + ":" + targetSlot;
                        }

                        @Override
                        public String getContainerText(PsiElement element, final String name) {
                            Wire wire = toWire((XmlTag) element);
                            if (wire == null) return null;

                            // TODO: because currently we only support instanceId check, so we ignore the group name check.
                            String sourceInstance = wire.getSourceInstance().getStringValue();
                            if (instanceId.equals(sourceInstance)) {
                                return "source";
                            } else {
                                return "target";
                            }
                        }

                        @Override
                        protected Icon getIcon(PsiElement element) {
                            return Icons.WIRE;
                        }
                    })
                    .setTooltipText("Navigate to related wires.")
                    .setPopupTitle("Related Wires")
                    .setTargets(relatedWires);

            result.add(builder.createLineMarkerInfo(element.getXmlTag()));
        }


    }

    void setResult(Collection<? super RelatedItemLineMarkerInfo> result) {
        this.result = result;
    }
}
