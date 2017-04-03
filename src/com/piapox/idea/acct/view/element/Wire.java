// Generated on Mon Dec 05 13:48:57 CET 2016
// DTD/Schema  :    null

package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;

/**
 * null:wireElemType interface.
 */
@NameStrategy(JavaNameStrategy.class)
public interface Wire extends DomElement {

	@NotNull
	GenericAttributeValue<String> getSourceInstance();


	@NotNull
	GenericAttributeValue<String> getSourceGroup();


	@NotNull
	@Required
	GenericAttributeValue<String> getSourceSlot();


	@NotNull
	GenericAttributeValue<String> getTargetInstance();


	@NotNull
	GenericAttributeValue<String> getTargetGroup();


	@NotNull
	@Required
	GenericAttributeValue<String> getTargetSlot();

}
