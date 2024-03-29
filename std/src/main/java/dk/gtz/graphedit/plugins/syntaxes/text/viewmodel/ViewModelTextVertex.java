package dk.gtz.graphedit.plugins.syntaxes.text.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.text.model.ModelTextVertex;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ViewModelTextVertex extends ViewModelVertex implements ISearchable {
	private final StringProperty text;

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
	public void addListener(ChangeListener<? super ViewModelVertex> listener) {
		super.addListener(listener);
		text.addListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void removeListener(ChangeListener<? super ViewModelVertex> listener) {
		super.addListener(listener);
		text.removeListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void addListener(InvalidationListener listener) {
		super.addListener(listener);
		text.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		super.addListener(listener);
		text.removeListener(listener);
	}

	@Override
	public void setValue(ViewModelVertex value) {
		super.setValue(value);
		if(value instanceof ViewModelTextVertex tvalue)
			text.setValue(tvalue.text().get());
	}

	@Override
	public void bind(ObservableValue<? extends ViewModelVertex> observable) {
		super.bind(observable);
		if(observable.getValue() instanceof ViewModelTextVertex tobs)
			text.bind(tobs.text());
	}

	@Override
	public void unbind() {
		super.unbind();
		text.unbind();
	}

	@Override
	public boolean isBound() {
		return super.isBound() || text.isBound();
	}

	@Override
	public void bindBidirectional(Property<ViewModelVertex> other) {
		super.bindBidirectional(other);
		if(other.getValue() instanceof ViewModelTextVertex tother)
			text.bindBidirectional(tother.text());
	}

	@Override
	public void unbindBidirectional(Property<ViewModelVertex> other) {
		super.unbindBidirectional(other);
		if(other.getValue() instanceof ViewModelTextVertex tother)
			text.unbindBidirectional(tother.text());
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
