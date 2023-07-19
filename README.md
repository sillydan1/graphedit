# graphedit
A graph editor for the 21st century
The hackable graph editor

## Core
Includes all the core functionality for GraphEdit...

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
 - [ ] Get feature parity with H-Uppaal
   - [x] Focus stealing
   - [ ] Project search (telescope-like)
   - [ ] clickable links
   - [ ] Run configurations (will replace engine "integrations")
 - [ ] Code polish and project structure cleanup
   - [ ] Separate codebase into projects (yalibs for libraries)
   - [ ] Fix todos
   - [ ] Combined modelling tool
   - [ ] graph editor polish
   - [ ] Tool keybinds
   - [ ] Custom keybinds
   - [ ] Project file pane
     - [ ] right-click menu
     - [ ] right-click new > model > type will create a new empty model file of the chosen type (don't open)
 - [ ] Beurocracy Cleanup
   - [ ] A documentation wiki 
   - [ ] Readme polish
   - [ ] Commission a logo
   - [ ] javadoc
   - [ ] Roadmap
   - [ ] Changelog
 - [ ] Release `v1.0.0` ([gradle publishing guide](https://www.jetbrains.com/help/space/publish-artifacts-from-a-gradle-project.html))
 - [ ] gitignored Project cache, such as what files did you have open last etc.
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

## Default
A set of default presentations, viewmodel and model objects to be injected as an example project

## Demo
A project meant for exploration and demonstration of the other libraries. Shouldn't be used and should be removed before `v1.0.0` is released.

