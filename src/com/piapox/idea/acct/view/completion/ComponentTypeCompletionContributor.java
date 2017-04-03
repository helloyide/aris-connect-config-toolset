package com.piapox.idea.acct.view.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.util.ProcessingContext;
import com.piapox.idea.acct.view.meta.ComponentMeta;
import org.jetbrains.annotations.NotNull;

import static com.piapox.idea.acct.util.XmlHelper.checkAttributeValuePosition;
import static com.piapox.idea.acct.view.util.ViewHelper.isViewConfiguration;

public class ComponentTypeCompletionContributor extends CompletionContributor {
    public ComponentTypeCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(XmlTokenImpl.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {

                        if (!isViewConfiguration(parameters.getOriginalFile())) return;

                        XmlTokenImpl position = (XmlTokenImpl) parameters.getPosition();
                        if (!checkAttributeValuePosition(
                                position, new String[]{"type"}, new String[]{"component", "layoutComponent"})) return;

                        ComponentMeta.getComponentTypes().forEach(
                                type -> result.addElement(LookupElementBuilder.create(type)));

                        // suppress other CompletionContributors come after
                        result.stopHere();
                    }
                });
    }

}
