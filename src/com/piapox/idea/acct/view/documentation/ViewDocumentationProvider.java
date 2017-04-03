package com.piapox.idea.acct.view.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.piapox.idea.acct.view.element.Component;
import com.piapox.idea.acct.view.element.ViewConfigElement;
import com.piapox.idea.acct.view.meta.ComponentMeta;
import com.piapox.idea.acct.view.util.ViewHelper;
import org.jetbrains.annotations.Nullable;

import static com.piapox.idea.acct.view.util.ViewHelper.toViewConfigElement;

public class ViewDocumentationProvider extends AbstractDocumentationProvider {

    @Nullable
    private String getComponentType(PsiElement originalElement) {
        if (ViewHelper.isViewConfiguration(originalElement.getContainingFile())) {
            XmlTag xmlTag;
            if (originalElement instanceof XmlTag) {
                xmlTag = (XmlTag) originalElement;
            } else if (originalElement.getParent() instanceof XmlTag) {
                xmlTag = (XmlTag) originalElement.getParent();
            } else {
                return null;
            }
            ViewConfigElement viewConfigElement = toViewConfigElement(xmlTag);
            if (viewConfigElement instanceof Component) {
                return ((Component) viewConfigElement).getType().getStringValue();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        String componentType = getComponentType(originalElement);
        return componentType == null ? null : ComponentMeta.getComponentShortDescription(componentType);
    }

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        String componentType = getComponentType(originalElement);
        return componentType == null ? null : ComponentMeta.getComponentHtmlDescription(componentType);
    }
}
