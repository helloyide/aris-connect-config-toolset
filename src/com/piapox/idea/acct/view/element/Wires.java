// Generated on Mon Dec 05 13:48:57 CET 2016
// DTD/Schema  :    null

package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * null:wiresElemType interface.
 */
public interface Wires extends DomElement {

	/**
	 * Returns the list of wire children.
	 * @return the list of wire children.
	 */
	@NotNull
	List<Wire> getWires();
	/**
	 * Adds new child to the list of wire children.
	 * @return created child
	 */
	Wire addWire();


}
