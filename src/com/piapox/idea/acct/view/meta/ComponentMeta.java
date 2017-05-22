package com.piapox.idea.acct.view.meta;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * We should read this map from configuration file in the future, so user can update them without changing code.
 * <p>
 * Follow information comes from the Technical Documentation:
 * https://iwiki.eur.ad.sag/display/Copernicus/Component+reference
 *
 * TODO: support ADS, ECP, UMC, etc... plugins
 * TODO: slots for Thin client components
 *
 */
public class ComponentMeta {

    private static ImmutableMap<String, String> COMPONENT_DESCRIPTIONS = ImmutableMap.<String, String>builder()
            // publishing
            .put("attributes", "Attribute table: List of maintained attributs for a model or object. If attribute value does not exist for actual language, but it exist for fallback language, than falback value is displayed.")
            .put("commentWrapper", "This component is used for embedding content in an iFrame. For example you can embedd a custom web-page like an intranet-startpage.")
            .put("databaseInfo", "This component shows an information dialog, if there is no database published.")
            .put("databaseSelection", "This component shows a select-box dialog if there is more than one ARIS database published.")
            .put("databaseSwitcher", "This component shows a select dialog within the administration section \"Publish\" of ARIS Connect Portal to select which ARIS databases should be published.")
            .put("editSwitch", "Enable and disable edit functionality for factsheets. Component is only available for ARIS Connect Designer or ARIS Connect Contributor license.It is visible in item view and in glossary view.")
            .put("filterSelection", "The role-based filters can be used to filter/reduce content within the ARIS Connect Portal. For instance the user has the possibility to see only published information which is related to his roles. \n" +
                    "If the role-based filters feature is active, you can open a dialog in the header menu to select or deselect filter criteria.")
            .put("glossary", "Glossary component shows table of all items of concrete item type, that is currently selected in the tree.\n" +
                    "It supports edit: adding and removing items for ARIS Connect Designer and ARIS Connect Contributor licenses.\n" +
                    "By default edit ability is disabled. The only way to enable it is via configuration.")
            .put("header", "This component displays the configured header domains.")
            .put("highlighting", "With the highlighting component it is possible  to mark objects within a model diagram  based on selected attribute value.")
            .put("image", "Displays a image. Optional with click areas and links to transformation items.")
            .put("item", "Content and layout of a fact sheet / overview tab")
            .put("linkToolbar", "component for creating link button to external web application with possibility to provide information like item GUID or item ID. Displaying link button is configurable and can be restricted by license and/or item type. There is also an option to choose a button icon.")
            .put("modelViewer", "This component shows the diagram.")
            .put("navigation", "This component generates a navigation tree, which looks like a group based explorer tree.")
            .put("nodepath", "This component shows the breadcrumb navigation.")
            .put("occurrences", "This component shows occurrences of selected object in the current and other models.")
            .put("processView", "This component displays the steps view.")
            .put("quickTable", "The Table view is a view which shows a process or activity flow in a table like style. Based on the first column, which is always a list of functions or activities, the next columns shows the connected objects. In the default configuration set the Table view shows the list of functions and the connected roles.")
            .put("raciTable", "This component displays the RACI view.")
            .put("recentlyChangedObjects", "This component shows a list of new process models and process models which were changed (by the logged in user) in a specified timeframe. This component is not based on transformation items, which means that it only shows objects of type \"model\" and a role-based filter has no influence on this component. It is also not possible to create instances of this component only for defined transformation items like \"Last changed processes, last changed documents, last changed organzational charts and so on\"")
            .put("relatedObjects", "This component shows the maintained relations ships of an object which is selected in the diagram.")
            .put("reports", "A dialog to select a report script, the output-format and start execution of the script")
            .put("reportsInfo", "A information dialog, to inform the user if a execution of an report script was finished.")
            .put("roleBasedItem", "The \"Important for me\"  or \"roleBasedItem\" component is a table  which lists all items, which are relevant to me as a user. For example, a list of all documents or processes which are relevant to me. The relevance is made by a connection between the own user ID or an user-group ID and the transformation item. ")
            .put("singleLevelTree", "The navigation tree, which shows only the navigation possibilities of one single level. Is used to build a hierarchy-based navigation.")
            // COP
            .put("action", "With the “action” component it is possible to create a list of links with images/icons.")
            .put("contact", "This component shows a list of administrator contacts a user has.")
            .put("cssCustomize", "With this component it is possible to change some predefined styles and fonts, upload a logo image file or restore to default settings of the user interface.")
            .put("extendedGlobalSearch", "This component shows the search page with a input field, the current search results for the different search areas (Portal, Models and objects, Collaboration, Documents).")
            .put("globalSearch", "This component shows the search widget showing search results for the different search areas (Portal, Models and objects, Collaboration, Documents).")
            .put("html", "A component to display HTML within a view.")
            .put("redirection", "A component to redirect a request to a defined URL.")
            .put("sendSignal", "A component, that sends a signal to another component.")
            .put("toolbar", "A small menu/icon bar")
            .put("topLevelNavigation", "Navigation entry point for the top-level of the portal. To switch for example between portal, collaboration and repository.")
            .put("userAction", "Drop-down menu  with user specific menu entries to different views. You can switch, for example, to administration or open the \"Download Clients\", \"Me profile\" or \"Languages\" page.")
            .put("wrapper", "The wrapper component makes it possible to integrate HTML pages via an iFrame into the view.")
            .put("searchFavorites", "This component shows the user defined search bookmarks, including their results. ")

            .build();

    private static ImmutableList<String> COMPONENT_TYPES = ImmutableList.<String>builder()
            // Thin client
            .add("THINCLIENT")
            .add("THINCLIENT_LIST")
            .add("THINCLIENT_NEW_MODEL")
            .add("THINCLIENT_BROWSE")
            .add("reportsInfo")
            .add("reports")
            // Publishing
            .add("item")
            .add("header")
            .add("databaseSwitcher")
            .add("navigation")
            .add("nodepath")
            .add("image")
            .add("quickTable")
            .add("raciTable")
            .add("singleLevelTree")
            .add("processView")
            .add("databaseSelection")
            .add("attributes")
            .add("roleBasedItem")
            .add("relatedObjects")
            .add("occurrences")
            .add("databaseInfo")
            .add("commentWrapper")
            .add("recentlyChangedObjects")
            .add("sscProperties")
            .add("sscItemTypes")
            .add("sscItemTypesGeneral")
            .add("publishingSearchProcessor")
            .add("publishingUrlProcessor")
            .add("highlighting")
            .add("filterSelection")
            .add("sscEditOverviewSheet")
            .add("sscItemListConfigure")
            .add("sscHierarchy")
            .add("sscViews")
            .add("sscTemplate")
            .add("sscManageResources")
            .add("sscComponents")
            .add("sscBaseDiagram")
            .add("sscBaseStepsView")
            .add("sscBaseVisualDiscovery")
            .add("sscLandingDiagram")
            .add("sscRecentlyChanged")
            .add("sscWrapper")
            .add("sscAppGallery")
            .add("sscLandingPage")
            .add("sscDebugger")
            .add("sscVisualDiscovery")
            .add("itemCollection")
            .add("modelViewer")
            .add("matrixModel")
            .add("glossary")
            .add("editSwitch")
            .add("linkToolbar")
            .add("visualDiscovery")
            .add("vdRepository")
            .add("vdAdminConfig")
            // Base
            .add("html")
            .add("wrapper")
            .add("loginDialog")
            .add("loginForm")
            .add("portalLogin")
            .add("toolbar")
            .add("topLevelNavigation")
            .add("globalSearch")
            .add("userAction")
            .add("signal_recorder")
            .add("extendedGlobalSearch")
            .add("searchFavorites")
            .add("sendSignal")
            .add("sscCssCustomize")
            .add("languageConfiguration")
            .add("reconfiguration")
            .add("switchConfig")
            .add("configItemHeader")
            .add("contact")
            .add("action")
            .add("versionRemover")
            .add("workspaceVersionEnforcer")
            .add("redirection")
            .add("sscFactSheet")
            .add("sscSubSheet")
            .add("viewNavigator")
            .add("debugger")
            .add("demoLayout")
            .add("demoLayout2")
            .add("demoSetComponentInfo")
            .add("umcProfile")
            .add("localizationCacheManager")
            .add("itemFavoritesSwitch")
            .add("logoUpload")
            // download client
            .add("downloadclients")

            .build();

    // sendSignal call in component
    private static ImmutableMultimap<String, String> SOURCE_SLOTS = ImmutableMultimap.<String, String>builder()
            // ABS
            .put("glossary", "selected")
            .put("header", "selectFirstHeaderEntry")
            .put("header", "activated")
            .put("header", "activatedNewTab")
            .put("header", "switchView")
            .put("highlighting", "highlightToggle")
            .put("highlighting", "highlightData")
            .put("image", "activated")
            .put("item", "activated")
            .put("item", "selected")
            .put("modelViewer", "activated")
            .put("modelViewer", "navigate")
            .put("navigation", "activated")
            .put("nodepath", "activated")
            .put("nodepath", "activatedNewTab")
            .put("occurrences", "open")
            .put("occurrences", "select")
            .put("processView", "activated")
            .put("recentlyChangedObjects", "activated")
            .put("relatedObjects", "selected")
            .put("reports", "reportCreated")
            .put("reportsInfo", "openDiagramTab")
            .put("reportsInfo", "openModelViewerReports")
            .put("roleBasedItem", "selected")
            .put("roleBasedItem", "activatedNewTab")
            .put("singleLevelTree", "activated")
            .put("sscItemTypesGeneral", "itemType")
            .put("sscViews", "openTemplate")
            .put("sscComponents", "selectedForEdit")
            .put("itemCollection", "selected")
            .put("matrixModel", "activated")
            .put("editSwitch", "edit")
            .put("THINCLIENT_NEW_MODEL", "activated")
            // COP
            .put("action", "openECP")
            .put("action", "switchHierarchyByIndex")
            .put("action", "openCreateModel")
            .put("extendedGlobalSearch", "publishing")
            .put("extendedGlobalSearch", "publishing_newTab")
            .put("extendedGlobalSearch", "thinClient")
            .put("extendedGlobalSearch", "thinClient_newTab")
            .put("extendedGlobalSearch", "ads_admin")
            .put("extendedGlobalSearch", "ads_admin_newTab")
            .put("extendedGlobalSearch", "collaboration")
            .put("extendedGlobalSearch", "collaboration_newTab")
            .put("extendedGlobalSearch", "umlConnect")
            .put("extendedGlobalSearch", "umlConnect_newTab")
            .put("globalSearch", "showAll")
            .put("globalSearch", "publishing")
            .put("globalSearch", "thinClient")
            .put("globalSearch", "ads_admin")
            .put("globalSearch", "collaboration")
            .put("globalSearch", "umlConnect")
            .put("sendSignal", "sig")
            .put("userAction", "activated")
            .put("searchFavorites", "publishing")
            .put("searchFavorites", "publishing_newTab")
            .put("searchFavorites", "thinClient")
            .put("searchFavorites", "thinClient_newTab")
            .put("searchFavorites", "ads_admin")
            .put("searchFavorites", "ads_admin_newTab")
            .put("searchFavorites", "collaboration")
            .put("searchFavorites", "collaboration_newTab")
            .put("switchConfig", "selectedForEdit")
            .put("configItemHeader", "backSelected")
            .put("workspaceVersionEnforcer", "out")
            .put("viewNavigator", "view")
            .put("umcProfile", "forProfileId")
            .put("localizationCacheManager", "invalidateCachedItem")
            .put("localizationCacheManager", "invalidateModificationCachedItem")
            .put("itemFavoritesSwitch", "open")

            .build();

    // receivSignal callback of components
    private static ImmutableMultimap<String, String> TARGET_SLOTS = ImmutableMultimap.<String, String>builder()
            // ABS
            .put("attributes", "open")
            .put("commentWrapper", "open")
            .put("databaseSwitcher", "open")
            .put("editSwitch", "open")
            .put("header", "open")
            .put("header", "switchHierarchyByIndex")
            .put("highlighting", "open")
            .put("item", "open")
            .put("item", "userSelected")
            .put("item", "userSelectAborted")
            .put("item", "adsDocumentSelected")
            .put("linkToolbar", "open")
            .put("modelViewer", "open")
            .put("modelViewer", "highlightData")
            .put("modelViewer", "highlightToggle")
            .put("modelViewer", "select")
            .put("navigation", "open")
            .put("nodepath", "open")
            .put("occurrences", "open")
            .put("occurrences", "showOccurrences")
            .put("processView", "open")
            .put("processView", "select")
            .put("quickTable", "open")
            .put("quickTable", "addColumn")
            .put("quickTable", "switchView")
            .put("raciTable", "open")
            .put("recentlyChangedObjects", "filterList")
            .put("relatedObjects", "open")
            .put("reports", "open")
            .put("reports", "executeReport")
            .put("reports", "setContextItem")
            .put("reportsInfo", "reportCreated")
            .put("reportsInfo", "navigateToReport")
            .put("singleLevelTree", "open")
            .put("sscLandingPage", "reinit")
            .put("itemCollection", "gallery")
            .put("matrixModel", "open")
            .put("matrixModel", "select")
            .put("glossary", "userSelected")
            .put("glossary", "userSelectAborted")
            .put("glossary", "adsDocumentSelected")
            .put("visualDiscovery", "changeContext")
            .put("visualDiscovery", "changeSelectionInDiagram")
            .put("THINCLIENT_NEW_MODEL", "show")
            // COP
            .put("extendedGlobalSearch", "open")
            .put("extendedGlobalSearch", "userSelected")
            .put("extendedGlobalSearch", "userSelectAborted")
            .put("extendedGlobalSearch", "adsDocumentSelected")
            .put("topLevelNavigation", "switchView")
            .put("wrapper", "open")
            .put("searchFavorites", "open")
            .put("loginDialog", "input")
            .put("toolbar", "setSignal")
            .put("toolbar", "triggerSlot")
            .put("toolbar", "deselectPFButtonSlot")
            .put("toolbar", "toggleDone")
            .put("configItemHeader", "show")
            .put("configItemHeader", "name")
            .put("workspaceVersionEnforcer", "in")

            .build();

    public static ImmutableCollection<String> getSourceSlots(String componentType) {
        return SOURCE_SLOTS.get(componentType);
    }

    public static ImmutableCollection<String> getTargetSlots(String componentType) {
        return TARGET_SLOTS.get(componentType);
    }

    @NotNull
    public static ImmutableList<String> getComponentTypes() {
        return COMPONENT_TYPES;
    }

    @Nullable
    public static String getComponentShortDescription(String componentType) {
        return COMPONENT_DESCRIPTIONS.get(componentType);
    }

    @Nullable
    public static String getComponentHtmlDescription(String componentType) {
        String description = COMPONENT_DESCRIPTIONS.get(componentType);
        if (description == null) return null;

        StringBuilder result = new StringBuilder();
        result.append("<p>").append(description).append("</p>");

        result.append("<h3>Source Slots</h3>");
        result.append("<ul>");
        for (String slot : getSourceSlots(componentType)) {
            result.append("<li>").append(slot).append("</li>");
        }
        result.append("</ul>");

        result.append("<h3>Target Slots</h3>");
        result.append("<ul>");
        for (String slot : getTargetSlots(componentType)) {
            result.append("<li>").append(slot).append("</li>");
        }
        result.append("</ul>");

        return result.toString();
    }

}
