package com.piapox.idea.acct.view.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.piapox.idea.acct.util.FileHelper.getFileNameWithoutExtension;
import static com.piapox.idea.acct.util.XmlHelper.checkAttributeValuePosition;
import static com.piapox.idea.acct.view.util.ViewHelper.getViewConfigFilesInConfigSet;
import static com.piapox.idea.acct.view.util.ViewHelper.isViewConfiguration;

public class ViewRefCompletionContributor extends CompletionContributor {
    public ViewRefCompletionContributor() {
        extend(
                CompletionType.BASIC,
                PlatformPatterns.psiElement(XmlTokenImpl.class),
                new CompletionProvider<CompletionParameters>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {

                        PsiFile psiFile = parameters.getOriginalFile();
                        if (!isViewConfiguration(psiFile)) return;

                        XmlTokenImpl position = (XmlTokenImpl) parameters.getPosition();
                        if (!checkAttributeValuePosition(position, new String[]{"ref"}, new String[]{"view"})) return;

                        Project project = position.getProject();

                        Set<String> availableInternalViewNames = new HashSet<>();
                        availableInternalViewNames.addAll(
                                getViewConfigFilesInConfigSet(project, psiFile.getVirtualFile(), true, false).stream().map(
                                        xmlFile -> getFileNameWithoutExtension(xmlFile.getName())).collect(Collectors.toList()));

                        availableInternalViewNames.forEach(
                                availableInternalViewName -> result.addElement(LookupElementBuilder.create(availableInternalViewName)));

                        // suppress other CompletionContributors come after
                        result.stopHere();
                    }
                });
    }

}
