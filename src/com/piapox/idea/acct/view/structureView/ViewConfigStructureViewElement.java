package com.piapox.idea.acct.view.structureView;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.xml.XmlTag;
import com.piapox.idea.acct.view.element.*;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.piapox.idea.acct.view.util.ViewHelper.*;


public class ViewConfigStructureViewElement implements StructureViewTreeElement {

    private final ViewConfigElement element;
    private final ItemPresentation presentation;
    private final NavigationItem navigationElement;

    public ViewConfigStructureViewElement(ViewConfigElement element) {
        this.element = element;
        navigationElement = (NavigationItem) element.getXmlTag().getNavigationElement();
        this.presentation = new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                // we need this to avoid exception when user just removes the element
                if (!element.isValid()) return "INVALID";

                String instanceId = element.getInstance().getStringValue();
                String instanceIdStr = instanceId == null ? "" : " (" + instanceId + ")";
                String elementType = getViewConfigElementType(element);
                switch (elementType) {
                    case "Component":
                        return ((Component) element).getType().getStringValue() + instanceIdStr;
                    case "Layout":
                        return getLayoutType((Layout) element) + instanceIdStr;
                    case "LayoutComponent":
                        return ((LayoutComponent) element).getType().getStringValue() + instanceIdStr;
                    case "View":
                        return getViewRef((View) element) + instanceIdStr;
                }
                return "UNKNOWN";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return getPresentableText();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                String elementType = getViewConfigElementType(element);
                switch (elementType) {
                    case "Component":
                        return Icons.COMPONENT;
                    case "Layout":
                        return Icons.LAYOUT;
                    case "LayoutComponent":
                        return Icons.LAYOUT_COMPONENT;
                    case "View":
                        return Icons.VIEW;
                }
                return null;
            }
        };
    }

    @Override
    public Object getValue() {
        return element;
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return presentation;
    }

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        List<TreeElement> treeElements = new ArrayList<>();

        // to keep the children order we have to use low level xml tag logic
        XmlTag xmlTag = element.getXmlTag();
        for (XmlTag subTag : xmlTag.getSubTags()) {
            ViewConfigElement viewConfigElement = toViewConfigElement(subTag);
            if (viewConfigElement != null) {
                treeElements.add(new ViewConfigStructureViewElement(viewConfigElement));
            }
        }

        return treeElements.toArray(new TreeElement[treeElements.size()]);
    }

    @Override
    public void navigate(boolean requestFocus) {
        navigationElement.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return navigationElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return navigationElement.canNavigateToSource();
    }
}
