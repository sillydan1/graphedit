package dk.gtz.graphedit.viewmodel;

import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.model.ModelVertex;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A view model vertex that contains text
 * @deprecated scheduled for removal. Do not use
 */
@Deprecated
public class ViewModelTextVertex extends ViewModelVertex implements ISearchable {
	private final StringProperty text;

	public ViewModelTextVertex(ViewModelPoint position, ViewModelVertexShape shape) {
		super(position, shape);
		this.text = new SimpleStringProperty();
	}

	public ViewModelTextVertex(ModelVertex vertex) {
		super(vertex);
		this.text = new SimpleStringProperty();
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var superObjects = new ArrayList<>(super.getInspectableObjects());
		superObjects.add(new InspectableProperty("Text", text));
		return superObjects;
	}

	@Override
	public List<String> getSearchValues() {
		return List.of(text.getValueSafe());
	}

	public StringProperty getTextProperty() {
		return text;
	}
}

