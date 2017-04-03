package com.piapox.idea.acct.util;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlTagImpl;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.containers.HashSet;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.impl.DomManagerImpl;
import com.piapox.idea.acct.view.element.Layout;
import com.piapox.idea.acct.view.element.LayoutComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static com.intellij.psi.xml.XmlTokenType.XML_TAG_END;

// TODO: check if we can use functions of PsiTreeUtil, XmlUtil
public class XmlHelper {

    /**
     * @return <code>true</code> if it's on the expect position
     */
    public static boolean checkAttributeValuePosition(XmlTokenImpl position, String[] expectAttributeNames, String[] expectTagNames) {
        XmlAttributeImpl attribute = getAttributeOfValuePosition(position);
        if (attribute == null) return false;
        String attributeName = attribute.getName();
        if (Arrays.stream(expectAttributeNames).anyMatch(expectAttributeName -> expectAttributeName.equals(attributeName))) {
            String xmlTagName = getXmlTagOfAttribute(attribute).getName();
            return Arrays.stream(expectTagNames).anyMatch(tagName -> tagName.equals(xmlTagName));
        }
        return false;
    }

    /**
     * When the cursor on an attribute value, get its xml attribute
     */
    @Nullable
    public static XmlAttributeImpl getAttributeOfValuePosition(XmlTokenImpl position) {
        if (position.getTokenType().equals(XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)) {
            CompositeElement treeParent = position.getTreeParent().getTreeParent();
            return treeParent instanceof XmlAttributeImpl ? (XmlAttributeImpl) treeParent : null;
        }
        return null;
    }

    /**
     * Get the xml tag which owns the attribute
     */
    public static XmlTagImpl getXmlTagOfAttribute(XmlAttributeImpl attribute) {
        return ((XmlTagImpl) attribute.getTreeParent());
    }

    @NotNull
    public static Set<String> collectDomElementAttributeValues(DomElement root, final String attributeName) {
        final Set<String> result = new HashSet<>();
        String attributeValue = root.getXmlTag().getAttributeValue(attributeName);
        if (attributeValue != null) {
            result.add(attributeValue);
        }

        root.acceptChildren(new DomElementVisitor() {
            @Override
            public void visitDomElement(DomElement element) {
                String value = element.getXmlTag().getAttributeValue(attributeName);
                if (value != null) {
                    result.add(value);
                }
            }

            public void visitLayout(Layout layout) {
                // recursive
                Set<String> found = collectDomElementAttributeValues(layout, attributeName);
                result.addAll(found);
            }

            public void visitLayoutComponent(LayoutComponent layoutComponent) {
                // recursive
                Set<String> found = collectDomElementAttributeValues(layoutComponent, attributeName);
                result.addAll(found);
            }

        });

        return result;
    }

    @Nullable
    public static DomElement convertToDomElement(PsiElement psiElement) {
        final DomManagerImpl domManager = DomManagerImpl.getDomManager(psiElement.getProject());
        final DomFileDescription description = domManager.getDomFileDescription(psiElement);
        if (description != null) {
            return getDomElement(psiElement, domManager);
        }
        return null;
    }

    @Nullable
    private static DomElement getDomElement(PsiElement psiElement, DomManager myDomManager) {
        if (psiElement instanceof XmlTag) {
            return myDomManager.getDomElement((XmlTag) psiElement);
        }
        if (psiElement instanceof XmlAttribute) {
            return myDomManager.getDomElement((XmlAttribute) psiElement);
        }
        return null;
    }

    @NotNull
    public static TextRange getOpenTagTextRange(XmlTag xmlTag) {
        Optional<PsiElement> openTagEnd = Arrays.stream(xmlTag.getChildren()).filter(
                psiElement -> psiElement instanceof XmlTokenImpl
                        && ((XmlTokenImpl) psiElement).getTokenType().equals(XML_TAG_END)
        ).findFirst();

        if (openTagEnd.isPresent()) {
            return TextRange.create(
                    xmlTag.getTextRange().getStartOffset(),
                    openTagEnd.get().getTextRange().getStartOffset());
        }
        return xmlTag.getTextRange();
    }

}
