// Generated on Mon Dec 05 13:48:57 CET 2016
// DTD/Schema  :    null

package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Layout extends ViewConfigElement {

    /**
     * Returns the list of component children.
     *
     * @return the list of component children.
     */
    @NotNull
    List<Component> getComponents();

    /**
     * Adds new child to the list of component children.
     *
     * @return created child
     */
    Component addComponent();


    /**
     * Returns the list of layoutComponent children.
     *
     * @return the list of layoutComponent children.
     */
    @NotNull
    @SubTagList("layoutComponent")
    List<LayoutComponent> getLayoutComponents();

    /**
     * Adds new child to the list of layoutComponent children.
     *
     * @return created child
     */
    @SubTagList("layoutComponent")
    LayoutComponent addLayoutComponent();


    /**
     * Returns the list of view children.
     *
     * @return the list of view children.
     */
    @NotNull
    List<View> getViews();

    /**
     * Adds new child to the list of view children.
     *
     * @return created child
     */
    View addView();


    /**
     * Returns the list of layout children.
     *
     * @return the list of layout children.
     */
    @NotNull
    List<Layout> getLayouts();

    /**
     * Adds new child to the list of layout children.
     *
     * @return created child
     */
    Layout addLayout();


}
