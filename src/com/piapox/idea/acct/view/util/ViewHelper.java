package com.piapox.idea.acct.view.util;

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.piapox.idea.acct.view.element.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.piapox.idea.acct.util.FileHelper.getConfigTypeName;

public class ViewHelper {

    /**
     * should check it at beginning for all stuffs
     * for in memory file it will return false.
     * interview.xml is not a view configuration
     */
    public static boolean isViewConfiguration(PsiFile psiFile) {
        if (!(psiFile instanceof XmlFile)) return false;

        // this can be null if the psiFile is only in memory,
        // for example when CompletionContributors call reference contributors
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null) return false;

        String extension = virtualFile.getExtension();
        if (!"xml".equals(extension)) return false;

        if ("interview.xml".equals(virtualFile.getName())) return false;

        String configTypeName = getConfigTypeName(virtualFile);
        return configTypeName != null && configTypeName.equals("views");
    }

    /**
     * the root parameter will also be checked
     *
     * @param instanceId if it's null or empty the function return null immediately.
     */
    @Nullable
    public static ViewConfigElement findViewConfigElementByInstanceId(ViewConfigElement root, String instanceId) {
        if (Strings.isNullOrEmpty(instanceId)) return null;
        if (instanceId.equals(root.getInstance().getStringValue())) return root;
        return findElementByInstanceIdImp(root, instanceId);
    }

    @Nullable
    private static ViewConfigElement findElementByInstanceIdImp(DomElement root, final String instanceId) {
        final ViewConfigElement[] result = new ViewConfigElement[1];
        root.acceptChildren(new DomElementVisitor() {
            @Override
            public void visitDomElement(DomElement element) {
                // Do nothing
            }

            public void visitLayout(Layout layout) {
                String foundInstanceId = layout.getInstance().getStringValue();
                if (instanceId.equals(foundInstanceId)) {
                    result[0] = layout;
                } else {
                    // recursive
                    ViewConfigElement found = findViewConfigElementByInstanceId(layout, instanceId);
                    if (found != null) {
                        result[0] = found;
                    }
                }
            }

            public void visitComponent(Component component) {
                String foundInstanceId = component.getInstance().getStringValue();
                if (instanceId.equals(foundInstanceId)) {
                    result[0] = component;
                }
            }

            public void visitView(View view) {
                String foundInstanceId = view.getInstance().getStringValue();
                if (instanceId.equals(foundInstanceId)) {
                    result[0] = view;
                }
            }

            public void visitLayoutComponent(LayoutComponent layoutComponent) {
                String foundInstanceId = layoutComponent.getInstance().getStringValue();
                if (instanceId.equals(foundInstanceId)) {
                    result[0] = layoutComponent;
                } else {
                    // recursive
                    ViewConfigElement found = findViewConfigElementByInstanceId(layoutComponent, instanceId);
                    if (found != null) {
                        result[0] = found;
                    }
                }
            }

        });

        return result.length > 0 ? result[0] : null;
    }

    @Nullable
    public static View getRootView(XmlFile xmlFile) {
        Project project = xmlFile.getProject();
        DomManager domManager = DomManager.getDomManager(project);
        DomFileElement<View> fileElement = domManager.getFileElement(xmlFile, View.class);
        return fileElement != null ? fileElement.getRootElement() : null;
    }

    /**
     * User may create a view from blank page, code completion will be triggered each time when user types
     * so we need to check the null and empty cases carefully.
     */
    @Nullable
    public static Layout getRootLayout(XmlFile xmlFile) {
        View rootView = getRootView(xmlFile);
        if (rootView == null) return null;
        List<Layout> layouts = rootView.getLayouts();
        if (layouts.isEmpty()) return null;
        return layouts.get(0);
    }

    public static boolean isRootView(View view) {
        return view.getParent() instanceof DomFileElement;
    }

    public static List<XmlFile> getViewConfigFilesInConfigSet(
            Project project, VirtualFile currentViewConfigFile, boolean includeGlobalConfigSet, boolean isTop) {

        List<XmlFile> result = new ArrayList<>();

        VirtualFile viewConfigFolder = currentViewConfigFile.getParent();
        if (viewConfigFolder == null) return result;
        String viewConfigPath = viewConfigFolder.getCanonicalPath();
        if (viewConfigPath == null) return result;

        VirtualFile currentConfigSetFolder = viewConfigFolder.getParent();
        if (currentConfigSetFolder == null) return result;
        VirtualFile configSetsFolder = currentConfigSetFolder.getParent();
        if (configSetsFolder == null) return result;
        String configSetsPath = configSetsFolder.getCanonicalPath();
        if (configSetsPath == null) return result;

        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        PsiManager psiManager = PsiManager.getInstance(project);

        List<VirtualFile> viewConfigFiles = new ArrayList<>();

        Arrays.stream(localFileSystem.list(viewConfigFolder)).forEach(viewConfigFileName -> {
            VirtualFile viewConfigFile = localFileSystem.findFileByPath(Paths.get(viewConfigPath, viewConfigFileName).toString());
            if (viewConfigFile != null) {
                viewConfigFiles.add(viewConfigFile);
            }
        });

        if (includeGlobalConfigSet) {
            String globalViewConfigPath = Paths.get(configSetsPath, "global", "views").toString();
            VirtualFile globalViewConfigFolder = localFileSystem.findFileByPath(globalViewConfigPath);
            if (globalViewConfigFolder != null) {
                Arrays.stream(localFileSystem.list(globalViewConfigFolder)).forEach(viewConfigFileName -> {
                    VirtualFile viewConfigFile = localFileSystem.findFileByPath(Paths.get(globalViewConfigPath, viewConfigFileName).toString());
                    if (viewConfigFile != null) {
                        viewConfigFiles.add(viewConfigFile);
                    }
                });
            }
        }

        viewConfigFiles.forEach(viewConfigFile -> {
            PsiFile psiFile = psiManager.findFile(viewConfigFile);
            if (ViewHelper.isViewConfiguration(psiFile)) {
                XmlFile xmlFile = (XmlFile) psiFile;
                View rootView = ViewHelper.getRootView(xmlFile);
                if (rootView != null) {
                    Boolean isTopView = rootView.getIstop().getValue();
                    // isTop = false by default
                    if (isTopView == null) {
                        isTopView = false;
                    }

                    if (isTop == isTopView) {
                        result.add(xmlFile);
                    }
                }
            }
        });


        return result;
    }

    public static String getLayoutType(Layout layout) {
        return layout.getXmlTag().getAttributeValue("xsi:type");
    }

    public static String getViewRef(View view) {
        return view.getXmlTag().getAttributeValue("ref");
    }

    @NotNull
    public static String getViewConfigElementType(ViewConfigElement element) {
        // cannot use Class.getSimpleName() because IDEA create wrapper for all elements
        if (element instanceof Component) {
            return "Component";
        } else if (element instanceof Layout) {
            return "Layout";
        } else if (element instanceof LayoutComponent) {
            return "LayoutComponent";
        } else if (element instanceof View) {
            return "View";
        }
        return "UNKNOWN";
    }

    @Nullable
    public static ViewConfigElement toViewConfigElement(XmlTag xmlTag) {
        DomManager domManager = DomManager.getDomManager(xmlTag.getProject());
        DomElement domElement = domManager.getDomElement(xmlTag);
        if (domElement != null && domElement instanceof ViewConfigElement) {
            return (ViewConfigElement) domElement;
        }
        return null;
    }

    @Nullable
    public static Wire toWire(XmlTag xmlTag) {
        DomManager domManager = DomManager.getDomManager(xmlTag.getProject());
        DomElement domElement = domManager.getDomElement(xmlTag);
        if (domElement != null && domElement instanceof Wire) {
            return (Wire) domElement;
        }
        return null;
    }

    @Nullable
    public static Property toProperty(XmlTag xmlTag) {
        DomManager domManager = DomManager.getDomManager(xmlTag.getProject());
        DomElement domElement = domManager.getDomElement(xmlTag);
        if (domElement != null && domElement instanceof Property) {
            return (Property) domElement;
        }
        return null;
    }

}
