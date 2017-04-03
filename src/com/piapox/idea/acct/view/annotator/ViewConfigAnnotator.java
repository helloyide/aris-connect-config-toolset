package com.piapox.idea.acct.view.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomElement;
import com.piapox.idea.acct.util.XmlHelper;
import com.piapox.idea.acct.view.util.ViewHelper;
import org.jetbrains.annotations.NotNull;

import static com.piapox.idea.acct.view.util.ViewHelper.isViewConfiguration;

/**
 * Here the most logic are copied from DefaultDomAnnotator, we cannot use it because there is a bug
 * https://intellij-support.jetbrains.com/hc/en-us/community/posts/115000088550-DomElementsAnnotator-vs-Annotator
 *
 */
public class ViewConfigAnnotator implements Annotator {

    private ViewConfigAnnotatorVisitor visitor = new ViewConfigAnnotatorVisitor();

    @Override
    public void annotate(@NotNull final PsiElement psiElement, @NotNull AnnotationHolder holder) {
        PsiFile containingFile = psiElement.getContainingFile();
        if (!isViewConfiguration(containingFile)) return;

        DomElement domElement = XmlHelper.convertToDomElement(psiElement);
        if (domElement != null) {
            visitor.setHolder(holder);
            visitor.setRootView(ViewHelper.getRootView((XmlFile) containingFile));
            domElement.accept(visitor);
        }
    }

}
