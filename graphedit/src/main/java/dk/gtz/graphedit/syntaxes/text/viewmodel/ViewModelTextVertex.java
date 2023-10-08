package dk.gtz.graphedit.syntaxes.text.viewmodel;

import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.syntaxes.text.model.ModelTextVertex;

public class ViewModelTextVertex extends ViewModelVertex implements ISearchable {
	private final StringProperty text;

	public ViewModelTextVertex(ViewModelVertex base) {
		this(base.position(), base.shape());
	}

	public ViewModelTextVertex(ViewModelPoint position, ViewModelVertexShape shape) {
		super(position, shape);
		this.text = new SimpleStringProperty("");
	}

	public ViewModelTextVertex(ModelVertex vertex) {
		super(vertex, new ViewModelVertexShape(ViewModelShapeType.OVAL));
		this.text = new SimpleStringProperty("");
		if(vertex instanceof ModelTextVertex textVertex)
			this.text.set(textVertex.label());
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Label Text", text));
		return inspectables;
	}

	@Override
	public List<String> getSearchValues() {
		return List.of(text.getValueSafe());
	}

	public StringProperty getTextProperty() {
		return text;
	}

	@Override
	public ModelVertex toModel() {
		return new ModelTextVertex(position().toModel(), text.getValueSafe());
	}
}

