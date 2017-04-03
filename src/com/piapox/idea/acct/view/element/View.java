// Generated on Mon Dec 05 13:48:57 CET 2016
// DTD/Schema  :    null

package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * null:viewElemType interface.
 */
public interface View extends ViewConfigElement {

    /**
     * Returns the value of the istop child.
     *
     * @return the value of the istop child.
     */
    @NotNull
    GenericAttributeValue<Boolean> getIstop();


    /**
     * Returns the value of the sscCustomizable child.
     *
     * @return the value of the sscCustomizable child.
     */
    @NotNull
    GenericAttributeValue<Boolean> getSscCustomizable();


    /**
     * Returns the value of the version child.
     *
     * @return the value of the version child.
     */
    @NotNull
    @Required
    GenericAttributeValue<String> getVersion();


    /**
     * Returns the value of the layout child.
     *
     * @return the value of the layout child.
     */
    @NotNull
    @Required
    List<Layout> getLayouts();


    /**
     * Returns the value of the wires child.
     *
     * @return the value of the wires child.
     */
    @NotNull
    @Required
    Wires getWires();

}
