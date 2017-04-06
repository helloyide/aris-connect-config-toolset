ARIS Connect Config Toolset
===================
ARIS Connect Config Toolset is an IntelliJ plugin for creating or editing ARIS Connect XML configurations. It helps to reduce errors and accelerate the customization process. It's still under developing, the current version focuses on the view configuration, it will support more configurations in the future.

Features
-------------
- Validation during typing, show fix suggestions.
- Build-in documentation, choose instead of typing
- Connect related items, navigation instead of text search
- Smart refactor
- (not yet done) Auto deploy and reload the configurations

Demo
-------------
Here is a short demo about the auto completion feature, it shows how easy to create a new home view:
![Create a new home view in 2 minutes](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/demo.gif)

Licesense
-------------
This plugin works with both IntelliJ Community and Ultimate version (2016.3 and above). IntelliJ Community has Apache 2.0 licesense, so does this plugin, which means it's open source and free to use.

Feature Details
-------------
Follow features are already implemented:
### Auto completion
The tool understands your configuration. Base on the context, it shows suggestion about values which make sense. It encourage you to choose instead of typing, this reduces typo and saves you time to check the documentation.

In this example the tool knows you want to type an internal view name, so it search the current config-set and the global config-set, suggest the available internal view names. You don't need to open the config folder manually and open each file to check if which one is an internal view. All are done automatically for you, just choose one you need.
![Auto completion demo](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/view_ref.gif)

### View hierarchy and smart search
This tool has a structure view on the left side bar. It shows the view hierarchy with different icon. Layouts are blue, components are green and internal views are purple. It gives you an overview of the view hierarchy and you can select the tree node to navigate in XML file.

Beside the normal text search (Ctrl + F) you can use a more smarter search with the shortcut Ctrl + F12, during your typing it reduces the result tree and you can choose the node with Keyboard Up and Down. Different than the full text search, it only searches the instance name, component type, layout type and view reference and ignores the comment out contents.
![View hierarchy and smart search demo](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/view_hierarchy.gif)

### Validation and auto fix
Errors are shown as red and the warn are yellow, possible issues (weak warn) are shown with an underline.
The tool doesn't only validate your configuration, it can also help you to fix them. Make sure the cursor is on the line with issues, click Alt + Enter, some fix suggestion will be pop up:

- Warn: Free layout children doesn't have any size properties, which means it will take full parent size, in most case this is not what you want, even that is what you expect, you should still set the size values with 0 explicitly to make the code clear. Auto fix: add the size property with 0, or make that child invisible by adding the size 1.

- Weak Warn: There is no dedicated instance configuration for that component, it's using the default one. Default instance configuration is designed as a fallback, we don't encourage to use it in normal use case. Auto fix: create a dedicated instance configuration with the name of the instance, its content is copied from the default one.

- Warn: Wire has incorrect source/target instance, cannot find anything with that instance id. Auto fix: remove the wire.

- Warn: View instance name is different than the filename. It's quite common when you clone a view but forget to change its instance property. Auto fix: rename the file or change the instance property base on filename.
![Auto fix demo1](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/free_layout_children_size.gif)
![Auto fix demo2](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/view_instance_and_file_name.gif)

### Rename
When you rename the instance property of a component, you need to update all related wires and also change the instance configuration file name if there is one. Now it's done automatically, move the cursor on the instance property of component definition or of wire source/target instance. Click Shift + F6 and type the new name in dialog. It will change all related content. 

If you want to undo the changes, click Ctrl + Z, not only the property is revert, all other related operations are also reverted, i.e. they are in the one transaction.
![Rename demo1](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/instance_refactor.gif)

### Navigate to related element/configuration
ARIS Connect configurations are not just XML file, they have relationship. For example the ref property of view tag is actually a view instance name. It's quite often you need to navigate between these configurations.

Currently only internal view reference and the source/target instance name are clickable. But I will add more and more navigation in later version. So always try Ctrl + Mouse Left Click when you want to open the referenced configuration.
![Navigation demo1](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/instance_wire_navigation.gif)

### Line markers
On the left side of some contents you can see one or more icons. You can click on them, it will navigate to the related configuration. Different than Ctrl + Mouse Left Click, these navigation have more information.

For example if you see a white "C" within a green circle icon, it means there is an instance configuration for that component. When you click on it, the instance configuration will be opened in a new tab. Not every component has an instance configuration, only which one has will get this line marker.

Another example is the wire line marker, if the component/layout/view has one or more wires, the icon shows up, after you click on it, it shows a list which is group by wire source and target. When you choose one, it will scroll down to that wire definition.
![Line markers demo1](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/wire_line_marker.gif)
![Line markers demo1](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/instance_config.gif)

### Build in documentation
The tools has build-in documentation. Just move the cursor to the component and click Ctrl + Q. Click the background to close the pop window.

Currently it only works for component and only contains a short description and which slot the component supports. 
![documentation demo](https://raw.githubusercontent.com/helloyide/aris-connect-config-toolset/master/gifs/component_document.gif)

