package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Required;
import org.jetbrains.annotations.NotNull;

public interface Property extends DomElement {

	@NotNull
	@Required
	GenericAttributeValue<String> getName();


	@NotNull
	@Required
	GenericAttributeValue<String> getValue();

}
