package com.piapox.idea.acct.view.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.piapox.idea.acct.view.meta.LayoutMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.piapox.idea.acct.util.XmlHelper.getAttributeOfValuePosition;
import static com.piapox.idea.acct.util.XmlHelper.getXmlTagOfAttribute;
import static com.piapox.idea.acct.view.util.ViewHelper.isViewConfiguration;

/**
 * Base on the current position in view hierarchy, decide which property can be used
 */
public class PropertyNameCompletionContributor extends CompletionContributor {
    public PropertyNameCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(XmlTokenImpl.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {

                        if (!isViewConfiguration(parameters.getOriginalFile())) return;

                        XmlTokenImpl position = (XmlTokenImpl) parameters.getPosition();
                        XmlTagImpl propertyXmlTag = getPropertyXmlTagFromPropertyNamePosition(position);
                        if (propertyXmlTag == null) return;

                        List<String> properties = new ArrayList<>();

                        String layoutType = getLayoutType(propertyXmlTag);
                        if (layoutType != null) {
                            properties.addAll(LayoutMeta.getLayoutProperties(layoutType));
                        }

                        String parentLayoutType = getParentLayoutType(propertyXmlTag);
                        if (parentLayoutType != null) {
                            properties.addAll(LayoutMeta.getLayoutChildrenProperties(parentLayoutType));
                        }

                        properties.forEach(
                                type -> result.addElement(LookupElementBuilder.create(type)));

                        // suppress other CompletionContributors come after
                        result.stopHere();
                    }
                });
    }

    /**
     * For the case the editing property is a layout property
     */
    @Nullable
    private String getLayoutType(XmlTagImpl propertyXmlTag) {
        XmlTag parentTag = propertyXmlTag.getParentTag();
        if (parentTag != null && "layout".equals(parentTag.getName())) {
            return parentTag.getAttributeValue("xsi:type");
        }
        return null;
    }

    /**
     * For the case the editing property is not a layout property
     * or there is another layout outside the current layout.
     */
    @Nullable
    private String getParentLayoutType(XmlTagImpl propertyXmlTag) {
        XmlTag parentTag = propertyXmlTag.getParentTag();
        if (parentTag != null) {
            XmlTag grandParentTag = parentTag.getParentTag();
            if (grandParentTag != null && "layout".equals(grandParentTag.getName())) {
                return grandParentTag.getAttributeValue("xsi:type");
            }
        }
        return null;
    }

    @Nullable
    private XmlTagImpl getPropertyXmlTagFromPropertyNamePosition(XmlTokenImpl position) {
        XmlAttributeImpl attribute = getAttributeOfValuePosition(position);
        if (attribute != null && attribute.getName().equals("name")) {
            XmlTagImpl xmlTag = getXmlTagOfAttribute(attribute);
            String xmlTagName = xmlTag.getName();
            if (xmlTagName.equals("property")) {
                return xmlTag;
            } else {
                return null;
            }
        }
        return null;
    }

}
