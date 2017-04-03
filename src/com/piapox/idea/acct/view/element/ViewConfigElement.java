package com.piapox.idea.acct.view.element;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ViewConfigElement extends DomElement {

    @NotNull
    GenericAttributeValue<String> getInstance();

    @NotNull
    GenericAttributeValue<String> getGroups();

    @NotNull
    List<Property> getProperties();

    Property addProperty();
}
