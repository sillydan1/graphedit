package dk.gtz.graphedit.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.BuildConfig;
import dk.gtz.graphedit.logging.Toast;
import dk.gtz.graphedit.plugins.lua.LuaUtils;
import dk.gtz.graphedit.spi.IPlugin;
import dk.gtz.graphedit.util.EditorActions;
import dk.gtz.graphedit.util.Keymap;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.Lua.LuaError;
import party.iroiro.luajava.Lua.LuaType;
import party.iroiro.luajava.luajit.LuaJit;

// TODO: Keyboard events, so lua can do quick and cool ass fuck keybindings
//       To do this, I think we might need a global keybind store, where each normal bind
//       would have to check if it has been overridden... Or maybe they register themselves... or something
public class LuaPlugin implements IPlugin {
	private static final Logger logger = LoggerFactory.getLogger(LuaPlugin.class);
	private static record SubPlugin(String name, String description, Runnable onStart, Runnable onDestroy) {}
	private final List<SubPlugin> subPlugins;
	private Lua runtime;

	public LuaPlugin() {
		this.subPlugins = new ArrayList<>();
	}

	@Override
	public void onInitialize() {
		runtime = new LuaJit(); // TODO: This should be based on settings
	}

	@Override
	public void onStart() throws RuntimeException {
		try {
			var configDir = EditorActions.getConfigDir();
			injectGlobalModule();
			loadInitScript(configDir);
			for(var plugin : subPlugins) {
				logger.trace("starting sub plugin '{}'", plugin.name());
				plugin.onStart().run();
			}
		} catch(Exception e) {
			runtime.close();
			logger.error("error while starting lua plugin: {}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private void loadInitScript(String configDir) throws Exception {
		var dir = configDir + File.separator + "lua";
		var file = new File(dir, "init.lua");
		if(!file.exists()) {
			logger.trace("init.lua script not found in '{}'", dir);
			return;
		}
		try(var s = new Scanner(file)) {
			logger.trace("loading init.lua script from '{}'", dir);
			var fileContent = new StringBuilder();
			for(; s.hasNextLine(); )
				fileContent.append(s.nextLine()).append("\n");
			var err = runtime.run(fileContent.toString());
			if(err != LuaError.OK)
				throw new Exception("error:\n%s".formatted(runtime.toString(-1)));
		}
	}

	private void injectGlobalModule() {
		var geModule = new HashMap<String, Object>();
		geModule.put("info", new HashMap<>(Map.ofEntries(
						Map.entry("name", BuildConfig.APP_NAME),
						Map.entry("version", BuildConfig.APP_VERSION),
						Map.entry("version_git_sha", BuildConfig.COMMIT_SHA_LONG),
						Map.entry("build_time", BuildConfig.BUILD_TIME)
						)));
		geModule.put("log", logger);
		geModule.put("cfg", DI.get(ViewModelEditorSettings.class));
		geModule.put("buf", DI.get(IBufferContainer.class));
		geModule.put("api", new HashMap<>(Map.ofEntries(
						Map.entry("toast", Toast.class),
						Map.entry("action", EditorActions.class),
						Map.entry("dep", DI.class),
						Map.entry("add_plugin", LuaUtils.wrap(this::addPlugin)),
						Map.entry("keymap", new HashMap<>(Map.ofEntries(
									Map.entry("set", LuaUtils.wrap((key, fn, desc) -> DI.get(Keymap.class).set(
												LuaUtils.convert(key, LuaType.STRING),
												LuaUtils.convert(fn, LuaType.FUNCTION, e -> () -> e.call()),
												LuaUtils.convert(desc, LuaType.STRING)))),
									Map.entry("set_in_category", LuaUtils.wrap((key, fn, desc, category) -> DI.get(Keymap.class).set(
												LuaUtils.convert(key, LuaType.STRING),
												LuaUtils.convert(fn, LuaType.FUNCTION, e -> () -> e.call()),
												LuaUtils.convert(desc, LuaType.STRING),
												LuaUtils.convert(category, LuaType.STRING))))
									))))));
		runtime.push(geModule);
		runtime.setGlobal("ge");
	}

	private void addPlugin(Map<String, Object> plugin) {
		if(!plugin.containsKey("name"))
			throw new IllegalArgumentException("plugin must have a 'name' key");
		if(!(plugin.get("name") instanceof String name))
			throw new IllegalArgumentException("plugin name must be a string");
		var description = "Lua Plugin";
		if(plugin.containsKey("description"))
			description = LuaUtils.convert(plugin.get("description"), LuaType.STRING);
		var onStart = (Runnable)() -> {};
		if(plugin.containsKey("on_start"))
			onStart = LuaUtils.convert(plugin.get("on_start"), LuaType.FUNCTION, e -> () -> e.call());
		var onDestroy = (Runnable)() -> {};
		if(plugin.containsKey("on_destroy"))
			onDestroy = LuaUtils.convert(plugin.get("on_destroy"), LuaType.FUNCTION, e -> () -> e.call());
		subPlugins.add(new SubPlugin(name, description, onStart, onDestroy));
	}

	@Override
	public void onDestroy() {
		for(var plugin : subPlugins) {
			logger.trace("destroying sub plugin '{}'", plugin.name());
			plugin.onDestroy().run();
		}
		runtime.close();
	}

	@Override
	public String getDescription() {
		return """
			Provides a Lua runtime for scripting.

			Allows users to write Lua as a scripting language for the application.
			Add/Edit an 'init.lua' script in the 'lua' directory in 
			the '%s' directory to start using this.
			""".formatted(EditorActions.getConfigDir());
	}

	@Override
	public String getName() {
		return "lua";
	}
}
