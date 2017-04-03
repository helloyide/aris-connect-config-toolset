package com.piapox.idea.acct.view.reference;

import com.intellij.openapi.project.Project;
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
import com.piapox.idea.acct.view.element.View;
import com.piapox.idea.acct.view.util.ViewHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.piapox.idea.acct.view.util.ViewHelper.getViewConfigFilesInConfigSet;
import static com.piapox.idea.acct.view.util.ViewHelper.isViewConfiguration;

public class ViewReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                XmlPatterns.xmlAttributeValue(),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext context) {

                        PsiFile psiFile = element.getContainingFile();
                        if (isViewConfiguration(psiFile)) {

                            XmlAttributeValue attributeValue = (XmlAttributeValue) element;
                            PsiElement attributeValueParent = attributeValue.getParent();
                            // for example: <?xml version="1.0", the parent of attribute value "1.0" is not XmlAttribute
                            if (attributeValueParent instanceof XmlAttribute) {
                                XmlAttribute attribute = (XmlAttribute) attributeValueParent;
                                String attributeName = attribute.getName();
                                if ("ref".equals(attributeName)) {
                                    XmlTag parent = attribute.getParent();
                                    if (parent != null && "view".equals(parent.getName())) {

                                        String internalViewId = attributeValue.getValue();
                                        Project project = element.getProject();

                                        List<XmlFile> internalViewConfigFiles = getViewConfigFilesInConfigSet(project, psiFile.getVirtualFile(), true, false);
                                        // need array for final variable access
                                        final XmlAttribute[] targetXmlAttribute = new XmlAttribute[1];
                                        Optional<XmlFile> found = internalViewConfigFiles.stream().filter(xmlFile -> {
                                            View rootView = ViewHelper.getRootView(xmlFile);
                                            if (rootView != null) {
                                                String viewInstance = rootView.getInstance().getStringValue();
                                                if (internalViewId.equals(viewInstance)) {
                                                    targetXmlAttribute[0] = rootView.getXmlTag().getAttribute("instance");
                                                    return true;
                                                }
                                            }
                                            return false;
                                        }).findFirst();

                                        if (found.isPresent()) {
                                            // this is how to jump to file directly, but we can jump to its instance attribute instead.
//                                            XmlFile targetInternalViewConfigFile = found.get();
//                                            return new PsiReference[]{new PsiReference() {
//                                                @Override
//                                                public PsiElement resolve() {
//                                                    return getElement();
//                                                }
//
//                                                @NotNull
//                                                @Override
//                                                public String getCanonicalText() {
//                                                    return targetInternalViewConfigFile.getName();
//                                                }
//
//                                                @Override
//                                                public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
//                                                    return null;
//                                                }
//
//                                                @Override
//                                                public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
//                                                    return null;
//                                                }
//
//                                                @Override
//                                                public boolean isReferenceTo(PsiElement element) {
//                                                    return false;
//                                                }
//
//                                                @NotNull
//                                                @Override
//                                                public Object[] getVariants() {
//                                                    return new Object[0];
//                                                }
//
//                                                @Override
//                                                public boolean isSoft() {
//                                                    return false;
//                                                }
//
//                                                @Override
//                                                public PsiElement getElement() {
//                                                    return targetInternalViewConfigFile;
//                                                }
//
//                                                @Override
//                                                public TextRange getRangeInElement() {
//                                                    return new TextRange(0, internalViewId.length());
//                                                }
//                                            }};

                                            XmlAttribute xmlAttribute = targetXmlAttribute[0];
                                            return new PsiReference[]{new XmlAttributeReference((XmlAttributeImpl) xmlAttribute) {
                                                @Override
                                                public PsiElement resolve() {
                                                    return getElement();
                                                }

                                                @Override
                                                public TextRange getRangeInElement() {
                                                    //noinspection ConstantConditions
                                                    return new TextRange(0, xmlAttribute.getValue().length());
                                                }
                                            }};
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
