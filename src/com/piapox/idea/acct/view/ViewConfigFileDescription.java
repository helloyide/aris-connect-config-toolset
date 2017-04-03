package com.piapox.idea.acct.view;


import com.intellij.util.xml.DomFileDescription;
import com.piapox.idea.acct.view.element.View;

public class ViewConfigFileDescription extends DomFileDescription<View> {
    public ViewConfigFileDescription() {
        super(View.class, "view", "");
    }
}
