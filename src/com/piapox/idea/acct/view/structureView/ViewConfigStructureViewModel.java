package com.piapox.idea.acct.view.structureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.piapox.idea.acct.view.element.ViewConfigElement;
import com.piapox.idea.acct.view.util.ViewHelper;
import org.jetbrains.annotations.NotNull;

import static com.piapox.idea.acct.view.util.ViewHelper.getViewConfigElementType;

public class ViewConfigStructureViewModel extends StructureViewModelBase implements
        StructureViewModel.ElementInfoProvider{

    public ViewConfigStructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile, new ViewConfigStructureViewElement(ViewHelper.getRootLayout((XmlFile) psiFile)));
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        ViewConfigElement viewConfigElement = (ViewConfigElement) element.getValue();
        String viewConfigElementType = getViewConfigElementType(viewConfigElement);
        return viewConfigElementType.equals("Component") || viewConfigElementType.equals("View");
    }
}
