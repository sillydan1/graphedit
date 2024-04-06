package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.yalibs.yafunc.IRunnable1;
import javafx.scene.input.KeyCombination;

public class Keymap {
    public static record Keybind(String category, Runnable action, String description) {}
    private final Map<KeyCombination, Keybind> keymap = new HashMap<>();
    private final List<IRunnable1<Map.Entry<KeyCombination, Keybind>>> newKeymapHandlers;
    private final List<IRunnable1<Map.Entry<KeyCombination, Keybind>>> overriddenKeymapHandlers;

    public Keymap() {
	this.newKeymapHandlers = new ArrayList<>();
	this.overriddenKeymapHandlers = new ArrayList<>();
    }

    public void set(String keycombination, Runnable action, String description) {
	set(keycombination, action, description, "keybinds");
    }

    public void set(String keycombination, Runnable action, String description, String category) {
	var key = KeyCombination.valueOf(keycombination);
	var value = new Keybind(category, action, description);
	var oldVal = keymap.put(key, value);
	if(oldVal != null)
	    overriddenKeymapHandlers.forEach(h -> h.run(Map.entry(key, oldVal)));
	newKeymapHandlers.forEach(h -> h.run(Map.entry(key, value)));
    }

    public Map<KeyCombination, Keybind> get() {
	return keymap;
    }

    public void onNewKeymap(IRunnable1<Map.Entry<KeyCombination, Keybind>> handler) {
	newKeymapHandlers.add(handler);
    }

    public void onKeymapOverridden(IRunnable1<Map.Entry<KeyCombination, Keybind>> handler) {
	overriddenKeymapHandlers.add(handler);
    }
}
