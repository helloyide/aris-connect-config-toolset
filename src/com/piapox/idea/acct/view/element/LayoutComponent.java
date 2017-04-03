// Generated on Mon Dec 05 13:48:57 CET 2016
// DTD/Schema  :    null

package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * null:layoutComponentElemType interface.
 */
public interface LayoutComponent extends ViewConfigElement {

    /**
     * Returns the value of the type child.
     *
     * @return the value of the type child.
     */
    @NotNull
    @Required
    GenericAttributeValue<String> getType();


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
