package com.piapox.idea.acct.view.lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import com.piapox.idea.acct.util.XmlHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.piapox.idea.acct.view.util.ViewHelper.isViewConfiguration;

public class WireLineMarkerProvider extends RelatedItemLineMarkerProvider {

    private WireLineMarkerVisitor visitor = new WireLineMarkerVisitor();

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {
        if (!isViewConfiguration(element.getContainingFile())) return;

        DomElement domElement = XmlHelper.convertToDomElement(element);
        if (domElement != null) {
            visitor.setResult(result);
            domElement.accept(visitor);
        }
    }
}
