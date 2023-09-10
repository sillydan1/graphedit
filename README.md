<!--
TODO: Add this when repo is public
<p align="center">
   <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-light.svg">
      <img alt="graphedit logo" width="700" height="256" src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/graphedit-logo-light.svg" style="max-width: 100%;">
   </picture>
</p>
-->
![img](.github/resources/logo/graphedit-logo-dark.svg)

## Build
The project is compiled using `gradle` but for the lazy ones we provide a simple `Makefile`:
```shell
# Compile and start the editor
make run
```

## Install
```shell
# TODO: figure out how we are going to distribute this
```

## Architecture

### Model View Viewmodel
This project is strongly influenced by the [MVVM (Model View ViewModel)](https://en.wikipedia.org/wiki/Model–view–viewmodel) design pattern and the classes and packages are named appropriately so.
 
### Core
Includes all the core functionality for Graphedit. This includes all the views, viewmodels, models and all the associtated business logic such as serializers and various tooling.

### Demo
The primary entrypoint for the default application. This is subject to change and may even be deleted soon.

