package dk.gtz.graphedit.plugins.syntaxes.text.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.text.model.ModelTextVertex;
import dk.gtz.graphedit.viewmodel.Autolisten;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ViewModelTextVertex extends ViewModelVertex implements ISearchable {
	@Autolisten
	public final StringProperty text;

	public StringProperty text() {
		return text;
	}

	public ViewModelTextVertex(UUID uuid, ViewModelVertex base) {
		this(uuid, base.position(), base.shape());
	}

	public ViewModelTextVertex(UUID uuid, ViewModelPoint position, ViewModelVertexShape shape) {
		super(uuid, position, shape);
		this.text = new SimpleStringProperty("");
	}

	public ViewModelTextVertex(UUID uuid, ModelVertex vertex) {
		super(uuid, vertex, new ViewModelVertexShape(ViewModelShapeType.OVAL));
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

	@Override
	public boolean equals(Object other) {
		if(!super.equals(other))
			return false;
		if(!(other instanceof ViewModelTextVertex vother))
			return false;
		return text.get().equals(vother.text.get());
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ text.get().hashCode();
	}
}
