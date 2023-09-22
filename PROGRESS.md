# Progress
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
 - [x] Beurocracy Cleanup
   - [x] A documentation wiki
   - [x] Commission a logo
   - [x] javadoc
      - [x] InspectorUtils.java:73: error: unexpected content
      - [x] InspectorUtils.java:81: error: @param name not found
      - [x] InspectorUtils.java:111: error: unexpected content
      - [x] InspectorUtils.java:148: error: unexpected content
      - [x] ShapeUtil.java:19: error: unknown tag: implNote
      - [x] ViewModelEdge.java:102: error: unexpected content
      - [x] ViewModelVertex.java:100: error: unexpected content
      - [x] ViewModelTextVertex.java:29: error: unexpected content
   - [x] Roadmap
   - [x] Readme polish
   - [x] Changelog
 - [x] CI
 - [x] Make repository public
   - [x] use gzip [see this](https://github.com/mzmine/mzmine3/issues/1063)
   - [x] dont install in /opt
   - [x] license
   - [x] /bin/graphedit > /bin/Graphedit
 - [ ] Release `v1.0.0` ([gradle publishing guide](https://www.jetbrains.com/help/space/publish-artifacts-from-a-gradle-project.html))
   - [x] Runtargets are not serializing
   - [ ] dont package /doc
   - [ ] Desktop icon not functioning on linux
   - [x] Delete key doesnt work on unified tool (misunderstanding - it was the select tool, which is on purpose)
   - [x] Ctrl+N dialogue not autoappending .json (same with new project)
   - [x] Preloader is ass - just launch into /tmp/graphedit/uuid and ask for savedir on first Ctrl+S (or something better)
   - [x] Change saved settings file notification to "saved /path/to/place/settings.json" instead
   - [x] MenuItems are lowercase, they should be Upper Case
   - [x] window title is just "java" (only when using "make run")
   - [x] trying to open project.json through filepane shouldn't be that scary
   - [x] inspector pane is odd to use (hide it)
   - [x] buffer changed *-thing
   - [x] Unread logs *-thing
   - [x] modals should have titles
   - [x] modals are ugly - missing padding
   - [x] autoselect unified tool
   - [ ] readme install steps
   - [ ] CD (needs a pre-release for prototyping purposes)
     - [ ] homebrew
     - [ ] flatpak
     - [ ] javadoc
 - [ ] gitignored Project cache, such as what files did you have open last etc.
 - [ ] Custom keybinds
 - [ ] Cut / Copy / Paste support
 - [ ] Additional Syntaxes
   - [ ] NTTA
   - [ ] HAWK
   - [ ] P/N
   - [ ] TIOA 
 - [ ] Release `v1.1.0
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

 ```sh
# (alpine)
apk add dpkg rpm gradle fakeroot git binutils gcc g++
git config --global --add safe.directory /graphedit
gradle jpackage --info
# (gradle:jammy)
apt install rpm dpkg dpkg-dev binutils git fakeroot
git config --global --add safe.directory /graphedit
gradle jpackage --info
 ```
