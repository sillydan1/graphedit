<p align="center">
   <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/logo/graphedit-logo-light.svg">
      <img alt="graphedit logo" width="700" height="256" src="https://raw.githubusercontent.com/sillydan1/graphedit/main/.github/resources/graphedit-logo-light.svg" style="max-width: 100%;">
   </picture>
</p>

![img](.github/resources/logo/graphedit-logo-dark.svg)

<details>
  <summary>Progess & Roadmap</summary>

**Progress**
 - [x] Basic graph model implementation
 - [x] Serialization / deserialization of the graph model
 - [x] Create issue on [jdtls](https://github.com/eclipse/eclipse.jdt.ls) repo about not implementing the [progress](https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#progress) feature - it's getting a bit annoying to use
 - [x] JFX basics
 - [x] Log4j / Logback
 - [x] BuildConfig
 - [x] Log sink for any type of log message (using log4j's verbosity enum)
 - [x] JFoenix (Nope)
 - [x] MaterialFX <-- Much better library (actively maintained)
 - [x] AtlantaFX <-- Even better, no need for special stuff
 - [x] Set up the core architecture (MVVM)
 - [x] Set up the basic window
   - [x] Tab pane
   - [x] project file explorer
   - [x] graph editor / viewer
   - [x] Toolbar
   - [x] properties / queries pane
   - [x] Log viewer
   - [x] Preloader
   - [x] Text-editable example vertex
   - [x] Project configuration & actual file-browser integration (geproject.json or something like that)
    - [x] Shortcut + Shift + O > file chooser > open project will restart the application and open the project
    - [x] double-clicking a model file will open the model
    - [x] double-clicking a directory will toggle the directory
    - [x] have a keyboard accelerator for new model file (create in selected path - warn if none selected)
 - [x] Get feature parity with H-Uppaal
   - [x] Focus stealing
   - [x] Project search (telescope-like)
   - [x] clickable links
   - [x] Run configurations (will replace engine "integrations")
 - [x] Code polish and project structure cleanup
   - [x] Separate codebase into projects (yalibs for libraries) [guide](https://central.sonatype.org/publish/publish-gradle/)
     - [x] `yadi`
     - [x] `yaerrors`
     - [x] `yafunc`
     - [x] `yastreamgobbler`
     - [x] `yaundo`
   - [x] Fix todos
   - [x] Combined modelling tool
   - [x] graph editor polish
   - [x] Project file pane
     - [x] Proper fill-out
 - [ ] Beurocracy Cleanup
   - [x] A documentation wiki
   - [x] Commission a logo
   - [ ] javadoc
   - [ ] Roadmap
   - [ ] Manpages
   - [ ] Changelog
   - [ ] Readme polish
 - [ ] Release `v1.0.0` ([gradle publishing guide](https://www.jetbrains.com/help/space/publish-artifacts-from-a-gradle-project.html))
 - [ ] gitignored Project cache, such as what files did you have open last etc.
 - [ ] Custom keybinds
 - [ ] Additional Syntaxes
   - [ ] NTTA
   - [ ] HAWK
   - [ ] P/N
   - [ ] TIOA 
 - [ ] Release `v1.1.0`
 - [ ] Trace-traverser & specification
 - [ ] LSP like specification (use docusaurus, or github wiki)
   - [ ] Protobuf specification (that way, you are language agnostic)
   - [ ] Implement `ILsp` / `ILspEngine` interfaces
   - [ ] Lint protobuf specification
   - [ ] Implement `ILint` / `ILinter` interfaces
 - [ ] Release `v1.2.0`
 - [ ] DAP like specification
   - [ ] Protobuf specification (that way, you are language agnostic)
   - [ ] Implement `IDap` / `IDapEngine` interfaces
 - [ ] Release `v1.3.0`
 - [ ] Add plugin API and [LuaJava](https://github.com/gudzpoz/luajava/tree/main)
   - [ ] Rewrite the default "plugins" as a lua plugin. This will simplify the codebase tremendously
 - [ ] Release `v2.0.0`

</details>

A graph editor for the 21st century / The hackable graph editor (not quite decided on the slogan yet)

## Build and Install
The project is compiled using `gradle`, but for the lazy ones, we provide a simple `Makefile`:

```shell
# Compile and start the editor
make run
```
 
## Core
Includes all the core functionality for GraphEdit...

## Default
A set of default presentations, viewmodel and model objects to be injected as an example project

## Demo
A project meant for exploration and demonstration of the other libraries. Shouldn't be used and should be removed before `v1.0.0` is released.

## CONTRIBUTION
This should be a separate file.

### Git
Commits should strive to follow the [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) format (merge commits nonwithstanding). Idealy each commit should at least be compileable and runnable as well, but with refactors, it can be understandable if this is not possible. A commit should represent a meaningful change, so "wip" commits are discouraged, and we encourage to squash and/or rename commits as needed.

#### PRs
Pull requests should be up-to-date with the target branch at review-time. Rebases are encouraged, but if target-branch merge commits are permitted as well when appropriate.

### Code Quality
Generally, try to follow the style that is already present and try to reuse existing components when appropriate.

#### Documentation
Ideally, any `public` thing should have a javadoc comment.

