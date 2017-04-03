package com.piapox.idea.acct.view.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.xml.XmlAttributeImpl;
import com.intellij.psi.impl.source.xml.XmlAttributeReference;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ProcessingContext;
import com.piapox.idea.acct.view.element.Layout;
import com.piapox.idea.acct.view.element.ViewConfigElement;
import org.jetbrains.annotations.NotNull;

import static com.piapox.idea.acct.view.util.ViewHelper.*;

public class WireReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {

                        if (isViewConfiguration(element.getContainingFile())){

                            XmlAttributeValue attributeValue = (XmlAttributeValue) element;
                            PsiElement attributeValueParent = attributeValue.getParent();
                            // for example: <?xml version="1.0", the parent of attribute value "1.0" is not XmlAttribute
                            if (attributeValueParent instanceof XmlAttribute) {
                                XmlAttribute attribute = (XmlAttribute) attributeValueParent;
                                String attributeName = attribute.getName();
                                if ("sourceInstance".equals(attributeName) || "targetInstance".equals(attributeName)) {
                                    XmlTag parent = attribute.getParent();
                                    if (parent != null && "wire".equals(parent.getName())) {

                                        String instanceId = attributeValue.getValue();
                                        Layout rootLayout = getRootLayout((XmlFile) element.getContainingFile());
                                        ViewConfigElement referencedElement = findViewConfigElementByInstanceId(rootLayout, instanceId);

                                        if (referencedElement != null) {
                                            XmlTag xmlTag = referencedElement.getXmlTag();
                                            if (xmlTag != null) {
                                                XmlAttributeImpl instanceAttribute = (XmlAttributeImpl) xmlTag.getAttribute("instance");
                                                if (instanceAttribute != null) {
                                                    return new PsiReference[]{new XmlAttributeReference(instanceAttribute) {
                                                        @Override
                                                        public PsiElement resolve() {
                                                            return getElement();
                                                        }

                                                        @Override
                                                        public TextRange getRangeInElement() {
                                                            return new TextRange(0, instanceId.length());
                                                        }
                                                    }};
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
