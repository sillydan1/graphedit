package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 * Viewmodel representation of {@link ModelLint}.
 * A Lint is a special kind of diagnostic annotation that can provide smart
 * meta-insights about sections of a graph.
 */
public class ViewModelLint implements IFocusable {
	private final StringProperty lintIdentifier;
	private final ObjectProperty<ModelLintSeverity> severity;
	private final StringProperty title;
	private final StringProperty message;
	private final ListProperty<UUID> affectedElements;
	private final ListProperty<ListProperty<ViewModelPoint>> affectedRegions;
	private final List<Runnable> focustHandlers;

	/**
	 * Construct a new viewmodel lint.
	 * 
	 * @param lint The model lint to base on
	 */
	public ViewModelLint(ModelLint lint) {
		this.lintIdentifier = new SimpleStringProperty(lint.lintIdentifier());
		this.severity = new SimpleObjectProperty<>(lint.severity());
		this.title = new SimpleStringProperty(lint.title());
		this.message = new SimpleStringProperty(lint.message());
		this.affectedElements = new SimpleListProperty<>(FXCollections.observableList(lint.affectedElements()));
		this.affectedRegions = new SimpleListProperty<>();
		this.focustHandlers = new ArrayList<>();
		for (var region : lint.affectedRegions()) {
			var l = new SimpleListProperty<>(FXCollections
					.observableList(region.stream().map(ViewModelPoint::new).toList()));
			this.affectedRegions.add(l);
		}
	}

	/**
	 * Get the unique identifier of the lint.
	 * 
	 * @return A string property with the lint's unique identifier.
	 */
	public StringProperty lintIdentifier() {
		return lintIdentifier;
	}

	/**
	 * Get the severity level of the lint
	 * 
	 * @return An object property with the severity level enumeration.
	 */
	public ObjectProperty<ModelLintSeverity> severity() {
		return severity;
	}

	/**
	 * Get the brief headline describing the lint.
	 * 
	 * @return A string property with the lint's title.
	 */
	public StringProperty title() {
		return title;
	}

	/**
	 * Get the lint's display message.
	 * 
	 * @return A string property with the lint's display message.
	 */
	public StringProperty message() {
		return message;
	}

	/**
	 * Get the list of affected vertices or edges.
	 * 
	 * @return A list property with the uuids of the affected syntactic elements.
	 */
	public ListProperty<UUID> affectedElements() {
		return affectedElements;
	}

	/**
	 * Get the list of affected regions.
	 * 
	 * @return A list property of lists of clock-wise sorted points, each
	 *         representing a polygon region.
	 */
	public ListProperty<ListProperty<ViewModelPoint>> affectedRegions() {
		return affectedRegions;
	}

	@Override
	public void addFocusListener(Runnable focusEventHandler) {
		focustHandlers.add(focusEventHandler);
	}

	@Override
	public void focus() {
		focustHandlers.forEach(Runnable::run);
	}
}
