package dk.gtz.graphedit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.yalibs.yafunc.IRunnable1;
import javafx.scene.input.KeyCombination;

/**
 * A class to manage keybindings.
 */
public class Keymap {
	/**
	 * A record to store a graphedit keybinding.
	 * 
	 * @param category    The category of the keybinding. Will not create a menubar
	 *                    item if empty.
	 * @param action      The action to run when the keybinding is pressed.
	 * @param description A short description of the keybinding. Will be used for
	 *                    menu item text in the menubar
	 */
	public static record Keybind(String category, Runnable action, String description) {
	}

	private final Map<KeyCombination, Keybind> keymap = new HashMap<>();
	private final List<IRunnable1<Map.Entry<KeyCombination, Keybind>>> newKeymapHandlers;
	private final List<IRunnable1<Map.Entry<KeyCombination, Keybind>>> overriddenKeymapHandlers;

	/**
	 * Construct a new Keymap instance.
	 */
	public Keymap() {
		this.newKeymapHandlers = new ArrayList<>();
		this.overriddenKeymapHandlers = new ArrayList<>();
	}

	/**
	 * Set a keybinding with no category.
	 * See {@link #set(String, Runnable, String, String)} for an example.
	 * 
	 * @param keycombination The keycombination to bind the action to.
	 * @param action         The action to run when the keycombination is pressed.
	 * @param description    A description of the action.
	 */
	public void set(String keycombination, Runnable action, String description) {
		set(keycombination, action, description, "");
	}

	/**
	 * Set a keybinding with a category. The category can be used to group
	 * keybindings and will be displayed in the top menubar.
	 * Note that if the keybinding is already set, the old value will be overridden.
	 *
	 * Example:
	 * 
	 * <pre>
	 * keymap.set("Shortcut+O", () -> EditorActions.openProject, "Open a project", "File");
	 * </pre>
	 *
	 * This will bind the action EditorActions.openProject to the keycombination
	 * Shortcut+O and display it under the 'File > Open a project' menu.
	 *
	 * @param keycombination The keycombination to bind the action to.
	 * @param action         The action to run when the keycombination is pressed.
	 * @param description    A description of the action.
	 * @param category       The category to group the keybinding under.
	 */
	public void set(String keycombination, Runnable action, String description, String category) {
		var key = KeyCombination.valueOf(keycombination);
		var value = new Keybind(category, action, description);
		var oldVal = keymap.put(key, value);
		if (oldVal != null)
			overriddenKeymapHandlers.forEach(h -> h.run(Map.entry(key, oldVal)));
		newKeymapHandlers.forEach(h -> h.run(Map.entry(key, value)));
	}

	/**
	 * Get the keymap.
	 * 
	 * @return The keymap.
	 */
	public Map<KeyCombination, Keybind> get() {
		return keymap;
	}

	/**
	 * Add a handler to be called when a new keymap is set.
	 * Note that this will also be called when a keymap is overridden.
	 * 
	 * @param handler The handler to add.
	 */
	public void onNewKeymap(IRunnable1<Map.Entry<KeyCombination, Keybind>> handler) {
		newKeymapHandlers.add(handler);
	}

	/**
	 * Add a handler to be called when a keymap is overridden.
	 * 
	 * @param handler The handler to add.
	 */
	public void onKeymapOverridden(IRunnable1<Map.Entry<KeyCombination, Keybind>> handler) {
		overriddenKeymapHandlers.add(handler);
	}
}
