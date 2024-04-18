<p align="center">
   <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-light.svg">
      <img alt="graphedit logo" width="700" height="256" src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-light.svg" style="max-width: 100%;">
   </picture>
</p>

<p align="center">
   Graphedit is an application for visualising, creating, editing and debugging graph-based languages
</p>

------

## Showcase 👀
<div style="display: flex;" align="center">
  <img src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/screenshots/Screenshot-light.png" alt="Image 1" width="48%" />
  <img src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/screenshots/Screenshot-dark.png" alt="Image 2" width="48%" />
</div>

## Build 💾
The project is compiled using `gradle` (version 7+):
```shell
# Just build the app
gradle build
# Compile and start the editor
gradle run
```
If you open the project in an editor and the class `BuildConfig` doesn't exist, simply running `gradle build` should autogenerate the class.

## Extend 🔌
Graphedit supports third party plugins. Take a look at the official [template repository](https://github.com/sillydan1/graphedit-plugin-template) as a starting point on creating your own plugin.
Also make sure to take a look at the [javadocs](https://javadoc.graphedit.gtz.dk) page for reference as well. If you want more of an example, take a look at how the [`std`](std/) plugin is structured.

### Lua Bindings 🌙
With graphedit version 1.5.0+ you can also extend the editor using the Lua programming language.
Simply add a file called `init.lua` in the graphedit configuration directory (see below) and start scripting! See the official [lua-types](https://github.com/sillydan1/graphedit-lua.nvim) repository for details on what is possible.

#### Where to put `init.lua` 📂
 - Windows: `%APPDATA%/graphedit/lua/init.lua`
 - Linux: `~/.local/graphedit/lua/init.lua`
 - MacOs: `~/Library/Application Support/Graphedit/lua/init.lua`

### The Centralized Plugin Database 📦
If you write a java plugin, you can submit `.jar` releases to the [plugin database](https://github.com/sillydan1/graphedit-plugindb) and the Plugin sidepanel will display it for all other Graphedit users as available for download!

## Contribute 🤝
We welcome all contributions. If you want to contribute, please take a look at the [contribution guidelines](CONTRIBUTION.md) and the [github project](https://github.com/users/sillydan1/projects/6/views/1) to see what needs to be done / is currently being worked on.

## Install
### Linux 🐧
We provide `.deb` and `.rpm` packaging formats directly from the github releases

<!-- #### Flatpak -->
<!-- Flatpak is not possible yet. Not enough github stars (yet) -->
<!-- ```shell -->
<!-- flatpak install sillydan1/graphedit/graphedit -->
<!-- ``` -->

#### Ubuntu or Debian
Download the `.deb` file from the latest github release
```shell
wget https://github.com/sillydan1/graphedit/releases/latest/download/graphedit.deb
dpkg -i graphedit.deb
```

#### RPM
Download the `.rpm` file from the latest github release
```shell
wget https://github.com/sillydan1/graphedit/releases/latest/download/graphedit.rpm
rpg -i graphedit.rpm
```

### OSX 🍎
After installing you may need to go into system settings > privacy settings and allow the app to be run. Alternatively, you can follow [this](https://support.apple.com/en-lk/guide/mac-help/mchleab3a043/mac) guide.
```shell
brew install sillydan1/graphedit/graphedit
```
or [download the .dmg](https://github.com/sillydan1/graphedit/releases/latest/download/graphedit.dmg) directly from the latest github release.

### Windows 🪟
Simply [Download the .msi](https://github.com/sillydan1/graphedit/releases/latest/download/graphedit.msi) installer from the latest github release and run it.

## Architecture 🏛️
Graphedit is designed from the ground up to be easy to extend with an opinionated, but easy to understand and efficient architecture.
You can extend the editor using plugins. Take a look at the [plugin github template](https://github.com/sillydan1/graphedit-plugin-template) to get started on writing your own plugin.

### Model View Viewmodel
This project is strongly influenced by the [MVVM (Model View ViewModel)](https://en.wikipedia.org/wiki/Model–view–viewmodel) design pattern and the classes and packages are named appropriately so.
 
### Core
Includes all the core functionality for Graphedit. This includes all the views, viewmodels, models and all the associtated business logic such as serializers and various tooling.

### Graphedit
The primary entrypoint for the default application. Doesn't do much other than start.

### std
The standard plugin that provides some additional language syntaxes and example language server implementations.
