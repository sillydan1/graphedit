<p align="center">
   <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-light.svg">
      <img alt="graphedit logo" width="700" height="256" src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-light.svg" style="max-width: 100%;">
   </picture>
</p>

<p align="center">
   Graphedit is an application for visualising, creating, editing and debugging graph-based syntaxes
</p>

------

## Build
The project is compiled using `gradle`:
```shell
# Just build the app
gradle build
# Compile and start the editor
gradle run
```
If you open the project in an editor and the class `BuildConfig` doesn't exist, simply running `gradle build` should autogenerate the class.

## Install
### Linux
We provide `.deb` and `.rpm` packaging formats directly from the github releases

<!-- #### Flatpak -->
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

### OSX
After installing you may need to go into system settings > privacy settings and allow the app to be run. Alternatively, you can follow [this](https://support.apple.com/en-lk/guide/mac-help/mchleab3a043/mac) guide.
```shell
brew install sillydan1/graphedit/graphedit
```
or [download the .dmg](https://github.com/sillydan1/graphedit/releases/latest/download/graphedit.dmg) directly from the latest github release.

### Windows
Simply [Download the .msi](https://github.com/sillydan1/graphedit/releases/latest/download/graphedit.msi) installer from the latest github release and run it.

## Showcase
<div style="display: flex;" align="center">
  <img src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/screenshots/Screenshot-light.png" alt="Image 1" width="48%" />
  <img src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/screenshots/Screenshot-dark.png" alt="Image 2" width="48%" />
</div>

## Architecture
Graphedit is designed from the ground up to be easy to extend with an opinionated, but easy to understand and efficient architecture.
Note that at the time of writing a plugin-system is planned, but not implemented yet. So extensions must be done via PRs at the moment.

### Model View Viewmodel
This project is strongly influenced by the [MVVM (Model View ViewModel)](https://en.wikipedia.org/wiki/Model–view–viewmodel) design pattern and the classes and packages are named appropriately so.
 
### Core
Includes all the core functionality for Graphedit. This includes all the views, viewmodels, models and all the associtated business logic such as serializers and various tooling.

### Graphedit
The primary entrypoint for the default application. Doesn't do much other than start.

