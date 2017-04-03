package com.piapox.idea.acct.view.meta;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * We should read this map from configuration file in the future, so user can update them without changing code.
 * <p>
 * Follow information comes from the Technical Documentation:
 * https://iwiki.eur.ad.sag/display/Copernicus/Summary
 *
 */
public class LayoutMeta {

    private static final String ALL_LAYOUT = "__ALL_LAYOUT__";

    private static ImmutableMultimap<String, String> LAYOUT_PROPERTIES = ImmutableMultimap.<String, String>builder()
            .put(ALL_LAYOUT, "signalHandling")
            .put("appStack", "tabHeight")
            .put("appStack", "tabWidth")
            .put("appStack", "toolbarWidth")
            .put("appStack", "toolbarDivId")
            .put("appStack", "navPosition")
            .put("flip", "animationDuration")
            .put("scroll", "overflowX")
            .put("scroll", "overflowY")
            .put("scroll", "scrollBarPlacement")
            .put("stack", "tabHeight")
            .put("stack", "tabWidth")
            .put("stack", "toolbarWidth")
            .put("stack", "toolbarDivId")
            .put("stack", "navPosition")
            .put("template", "template")

            .build();

    private static ImmutableMultimap<String, String> LAYOUT_CHILDREN_PROPERTIES = ImmutableMultimap.<String, String>builder()
            .put(ALL_LAYOUT, "className")
            .put(ALL_LAYOUT, "cssStyle")
            .put(ALL_LAYOUT, "padding")
            .put(ALL_LAYOUT, "signalForItemType")
            .put("appStack", "mainComponentId")
            .put("appStack", "tabLabel")
            .put("appStack", "asToolbar")
            .put("appStack", "toolbarComponentId")
            .put("appStack", "forItemType")
            .put("appHideable", "forItemType")
            .put("flip", "flipPosition")
            .put("flip", "minSize")
            .put("flip", "snapClosedSize")
            .put("flip", "isClosed")
            .put("flip", "width")
            .put("flip", "height")
            .put("free", "top")
            .put("free", "left")
            .put("free", "bottom")
            .put("free", "right")
            .put("free", "width")
            .put("free", "height")
            .put("free", "zIndex")
            .put("fixSize", "width")
            .put("fixSize", "height")
            .put("hideable", "forItemType")
            .put("stack", "mainComponentId")
            .put("stack", "tabLabel")
            .put("stack", "asToolbar")
            .put("stack", "toolbarComponentId")
            .put("stack", "forItemType")
            .put("template", "tplContainerId")

            .build();

    public static ImmutableCollection<String> getLayoutProperties(String layoutType) {
        return ImmutableSet.<String>builder()
                .addAll(LAYOUT_PROPERTIES.get(ALL_LAYOUT))
                .addAll(LAYOUT_PROPERTIES.get(layoutType))
                .build();
    }

    public static ImmutableCollection<String> getLayoutChildrenProperties(String layoutType) {
        return ImmutableSet.<String>builder()
                .addAll(LAYOUT_CHILDREN_PROPERTIES.get(ALL_LAYOUT))
                .addAll(LAYOUT_CHILDREN_PROPERTIES.get(layoutType))
                .build();
    }

}
