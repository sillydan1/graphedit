package dk.gtz.graphedit.plugins.view;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.Lua.LuaError;
import party.iroiro.luajava.luajit.LuaJit;

public class GeLuaPluginPanelController extends StackPane {
    private static final Logger logger = LoggerFactory.getLogger(GeLuaPluginPanelController.class.getName());
    private final VBox root;

    public GeLuaPluginPanelController() {
	root = new VBox();
	initialize();
    }

    private void initialize() {
	getChildren().add(root);
	addButton("Hello World", this::helloJava);
	addButton("Hello Logger", this::helloLogger);
	addButton("Java Calling Lua", this::callLuaFunction);
	addButton("Get Function and Object", this::getFunctionAndObject);
	addButton("load and use Script", this::loadLuaScriptAndUseIt);
	addButton("Errors", this::handleErrors);
	addButton("Add Label From Lua", this::addLabel);
    }

    private void addButton(String title, Runnable action) {
	var btn = new Button(title);
	btn.setOnAction(e -> action.run());
	root.getChildren().add(btn);
    }

    private void helloJava() {
	try (var l = new LuaJit()) {
	    l.openLibraries();
	    l.run("System = java.import('java.lang.System')");
	    l.run("System.out:println('Hello Java from Lua!')");
	    l.run("print('Hello Lua from Lua!')");
	}
    }

    private void helloLogger() {
	try (var l = new LuaJit()) {
	    l.pushJavaObject(logger);
	    l.setGlobal("logger");
	    l.run("logger:info('Hello Logger from Lua!')");
	    l.register("logSomething", this::logSomething);
	    l.run("logSomething()");
	}
    }

    private int logSomething(Lua l) {
	logger.info("logging something");
	return 0;
    }

    private void callLuaFunction() {
	try (var l = new LuaJit()) {
	    l.run("function helloJava() return 'hello java' end");
	    var results = l.execute("return helloJava()");
	    logger.info("helloJava gave {} results", results.length);
	    for(var result : results)
		logger.info("helloJava gave {}: '{}'", result.type(), (String)result.toJavaObject());
	    // NOTE: There is also something called pCall
	}
    }

    private void getFunctionAndObject() {
	try (var l = new LuaJit()) {
	    l.run("function helloJava() return 'hello java' end");
	    l.getGlobal("helloJava");
	    logger.info("helloJava is {} and the java value is '{}'", l.type(-1), l.toObject(-1));

	    l.run("a = 123");
	    l.getGlobal("a");
	    logger.info("a is {} = '{}'", l.type(-1), l.toNumber(-1));
	}
    }

    private void loadLuaScriptAndUseIt() {
	try (var l = new LuaJit()) {
	    l.pushJavaObject(logger);
	    l.setGlobal("logger");
	    var scriptContent = new String(getClass().getResource("example.lua").openStream().readAllBytes(), "UTF-8");
	    logger.info("loading the script");
	    l.load(scriptContent);
	    logger.info("running the script");
	    l.run(scriptContent);
	    var results = l.execute("Hello()");
	    if(results != null) {
		logger.info("Hello() gave {} results", results.length);
		for(var result : results)
		    logger.info("Hello() gave {}: '{}'", result.type(), (String)result.toJavaObject());
	    } else
		logger.info("Hello() gave no results");
	} catch (IOException e) {
	    logger.error("Error reading lua script", e);
	}
    }

    private void handleErrors() {
	try (var l = new LuaJit()) {
	    var err = l.run("error('this is an error')");
	    if(err != LuaError.OK)
		logger.error("lua {} script: {}", err, l.toString(-1));
	}
    }

    // Hack function to get past the java module exporting restrictions bullshit
    private int addChild(Lua l) {
	// NOTE: Reverse order. Because it's a stack (duh)
	var arg1 = l.toJavaObject(-2);
	if (!(arg1 instanceof Pane pane)) {
	    logger.error("First argument is not a Pane: {}", arg1);
	    return 1;
	}
	var arg2 = l.toJavaObject(-1);
	if (!(arg2 instanceof Node node)) {
	    logger.error("Second argument is not a Node: {}", arg2);
	    return 1;
	}
	pane.getChildren().add(node);
	return 0;
    }

    private void addLabel() {
	try (var l = new LuaJit()) {
	    l.openLibraries();
	    l.pushJavaObject(logger);
	    l.setGlobal("logger");
	    l.register("addChild", this::addChild);
	    var script = """
		Label = java.import('javafx.scene.control.Label')
		Button = java.import('javafx.scene.control.Button')
		VBox = java.import('javafx.scene.layout.VBox')
		Node = java.import('javafx.scene.Node')
		function GetACoolJavaFxObject()
		local l = Label('my handler is garbage collected by the time you see this!')
		local b = Button('Click me to crash')
		b:setOnAction(function(e) logger:info('Button clicked!') end)
		local children = java.array(Node, 2)
		-- NOTE: lua is 1-indexed...
		children[1] = b
		children[2] = l
		local x = VBox(children)
		-- NOTE: This gives java.lang.IllegalAccessException
		-- x:getChildren():add(Label('I am a child of the VBox'))
	        -- NOTE: This is a workaround the java module exporting restrictions
	        -- addChild(x, l)
		-- addChild(x, b)
		return x
		end
	    """;

	    runAndThrow(l, script);
	    var results = l.execute("return GetACoolJavaFxObject()");
	    if(results == null) {
		logger.error("GetACoolJavaFxObject() gave no results: {}", l.toString(-1));
		return;
	    }
	    for(var result : results) {
		var resultObject = result.toJavaObject();
	    if(resultObject instanceof Node node)
		root.getChildren().add(node);
	    }
	}
    }

    private void runAndThrow(Lua l, String script) {
	var err = l.run(script);
	if(err != LuaError.OK)
	    throw new RuntimeException(l.toString(-1));
    }
}
