package com.piapox.idea.acct.view.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.DomElement;
import com.piapox.idea.acct.view.element.Component;
import com.piapox.idea.acct.view.element.Layout;
import com.piapox.idea.acct.view.element.LayoutComponent;
import com.piapox.idea.acct.view.util.ViewHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.piapox.idea.acct.util.XmlHelper.*;
import static com.piapox.idea.acct.view.meta.ComponentMeta.getSourceSlots;
import static com.piapox.idea.acct.view.meta.ComponentMeta.getTargetSlots;
import static com.piapox.idea.acct.view.util.ViewHelper.findViewConfigElementByInstanceId;
import static com.piapox.idea.acct.view.util.ViewHelper.getRootLayout;

/**
 * Show completion for wire source/target instance/group, source/target slot
 */
public class WireCompletionContributor extends CompletionContributor {
    public WireCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(XmlTokenImpl.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {

                        PsiFile psiFile = parameters.getOriginalFile();
                        if (!ViewHelper.isViewConfiguration(psiFile)) return;

                        XmlTokenImpl position = (XmlTokenImpl) parameters.getPosition();
                        Layout rootLayout = getRootLayout((XmlFile) psiFile);
                        if (rootLayout == null) return;

                        if (isOnWireSourceSlotPosition(position)) {
                            String sourceInstance = getXmlTagOfAttribute(getAttributeOfValuePosition(position)).getAttributeValue("sourceInstance");
                            if (sourceInstance != null) {
                                DomElement foundDomElement = findViewConfigElementByInstanceId(rootLayout, sourceInstance);
                                if (foundDomElement instanceof Component) {
                                    Component foundComponent = (Component) foundDomElement;
                                    String type = foundComponent.getType().getStringValue();
                                    getSourceSlots(type).forEach(
                                            sourceSlot -> result.addElement(LookupElementBuilder.create(sourceSlot)));
                                } else if (foundDomElement instanceof LayoutComponent) {
                                    LayoutComponent foundLayoutComponent = (LayoutComponent) foundDomElement;
                                    String type = foundLayoutComponent.getType().getStringValue();
                                    getSourceSlots(type).forEach(
                                            sourceSlot -> result.addElement(LookupElementBuilder.create(sourceSlot)));
                                }
                            }
                            result.stopHere();

                        } else if (isOnWireTargetSlotPosition(position)) {
                            String targetInstance = getXmlTagOfAttribute(getAttributeOfValuePosition(position)).getAttributeValue("targetInstance");
                            if (targetInstance != null) {
                                DomElement foundDomElement = findViewConfigElementByInstanceId(rootLayout, targetInstance);
                                if (foundDomElement instanceof Component) {
                                    Component foundComponent = (Component) foundDomElement;
                                    String type = foundComponent.getType().getStringValue();
                                    getTargetSlots(type).forEach(
                                            targetSlot -> result.addElement(LookupElementBuilder.create(targetSlot)));
                                } else if (foundDomElement instanceof LayoutComponent) {
                                    LayoutComponent foundLayoutComponent = (LayoutComponent) foundDomElement;
                                    String type = foundLayoutComponent.getType().getStringValue();
                                    getTargetSlots(type).forEach(
                                            targetSlot -> result.addElement(LookupElementBuilder.create(targetSlot)));
                                }
                            }
                            result.stopHere();

                        } else if (isOnWireSourceOrTargetInstancePosition(position)) {
                            Set<String> instances = collectDomElementAttributeValues(rootLayout, "instance");
                            instances.forEach(instance -> result.addElement(LookupElementBuilder.create(instance)));
                            result.stopHere();

                        } else if (isOnWireSourceOrTargetGroupPosition(position)) {
                            Set<String> groups = collectDomElementAttributeValues(rootLayout, "groups");
                            groups.forEach(group -> result.addElement(LookupElementBuilder.create(group)));
                            result.stopHere();

                        }

                    }
                });
    }

    private boolean isOnWireSourceOrTargetInstancePosition(XmlTokenImpl position) {
        return checkAttributeValuePosition(position, new String[]{"sourceInstance", "targetInstance"}, new String[]{"wire"});
    }

    private boolean isOnWireSourceOrTargetGroupPosition(XmlTokenImpl position) {
        return checkAttributeValuePosition(position, new String[]{"sourceGroup", "targetGroup"}, new String[]{"wire"});
    }

    private boolean isOnWireSourceSlotPosition(XmlTokenImpl position) {
        return checkAttributeValuePosition(position, new String[]{"sourceSlot"}, new String[]{"wire"});
    }

    private boolean isOnWireTargetSlotPosition(XmlTokenImpl position) {
        return checkAttributeValuePosition(position, new String[]{"targetSlot"}, new String[]{"wire"});
    }

}
