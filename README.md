# graphedit
A graph editor for the 21st century

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
 - [ ] Set up the basic window
   - [x] Tab pane
   - [x] project file explorer
   - [ ] graph editor / viewer
   - [ ] Toolbar
   - [ ] properties / queries pane
   - [ ] Log viewer
   - [ ] Preloader
   - [ ] graph editor polish
 - [ ] Project configuration
 - [ ] Get feature parity with H-Uppaal
   - [ ] Project search
   - [ ] Good logging
   - [ ] Run configurations (will replace engine "integrations")
 - [ ] Seperate into projects, polish and project structure cleanup
 - [ ] Release `v1.0.0`
 - [ ] JNI interface for direct engine interaction
 - [ ] Lint specification
 - [ ] LSP like specification (use docusaurus, or github wiki)
 - [ ] `ILint` and `ILinter` engine

## Default
A set of default presentations, viewmodel and model objects to be injected as an example project

## Demo
A project meant for exploration and demonstration of the other libraries. Shouldn't be used and should be removed before `v1.0.0` is released.

