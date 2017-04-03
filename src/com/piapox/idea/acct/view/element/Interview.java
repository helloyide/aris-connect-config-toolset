// Generated on Mon Dec 05 13:48:57 CET 2016
// DTD/Schema  :    null

package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

/**
 * null:interviewElemType interface.
 */
public interface Interview extends DomElement {

	/**
	 * Returns the value of the version child.
	 * @return the value of the version child.
	 */
	@NotNull
	@Required
	GenericAttributeValue<String> getVersion();


	/**
	 * Returns the value of the wires child.
	 * @return the value of the wires child.
	 */
	@NotNull
	@Required
	Wires getWires();


}
