package dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelShapeType;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.gtz.graphedit.viewmodel.ViewModelVertexShape;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ViewModelPlace extends ViewModelVertex implements ISearchable {
	private final IntegerProperty initialTokenCount;

	public IntegerProperty initialTokenCount() {
		return initialTokenCount;
	}

	public ViewModelPlace(UUID uuid, ModelVertex vertex) {
		super(uuid, vertex, new ViewModelVertexShape(ViewModelShapeType.OVAL));
		initialTokenCount = new SimpleIntegerProperty(0);
		if(vertex instanceof ModelPlace tvertex)
			initialTokenCount.set(tvertex.initialTokenCount());
	}

	public ViewModelPlace(UUID uuid, ViewModelVertex vertex) {
		this(uuid, vertex.toModel());
	}

	@Override
	public List<String> getSearchValues() {
		return List.of("tokens: %d".formatted(initialTokenCount.get()));
	}

	@Override
	public ModelPlace toModel() {
		return new ModelPlace(position().getValue().toModel(), initialTokenCount.get());
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Initial Token Count", initialTokenCount));
		return inspectables;
	}

	@Override
	public void addListener(ChangeListener<? super ViewModelVertex> listener) {
		super.addListener(listener);
		initialTokenCount.addListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void removeListener(ChangeListener<? super ViewModelVertex> listener) {
		super.addListener(listener);
		initialTokenCount.removeListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void addListener(InvalidationListener listener) {
		super.addListener(listener);
		initialTokenCount.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		super.addListener(listener);
		initialTokenCount.removeListener(listener);
	}

	@Override
	public void setValue(ViewModelVertex value) {
		super.setValue(value);
		if(value instanceof ViewModelPlace tvalue)
			initialTokenCount.setValue(tvalue.initialTokenCount().get());
	}

	@Override
	public void bind(ObservableValue<? extends ViewModelVertex> observable) {
		super.bind(observable);
		if(observable.getValue() instanceof ViewModelPlace tobs)
			initialTokenCount.bind(tobs.initialTokenCount());
	}

	@Override
	public void unbind() {
		super.unbind();
		initialTokenCount.unbind();
	}

	@Override
	public boolean isBound() {
		return super.isBound() && initialTokenCount.isBound();
	}

	@Override
	public void bindBidirectional(Property<ViewModelVertex> other) {
		super.bindBidirectional(other);
		if(other.getValue() instanceof ViewModelPlace tother)
			initialTokenCount.bindBidirectional(tother.initialTokenCount());
	}

	@Override
	public void unbindBidirectional(Property<ViewModelVertex> other) {
		super.unbindBidirectional(other);
		if(other.getValue() instanceof ViewModelPlace tother)
			initialTokenCount.unbindBidirectional(tother.initialTokenCount());
	}

	@Override
	public boolean equals(Object other) {
		if(!super.equals(other))
			return false;
		if(!(other instanceof ViewModelPlace vother))
			return false;
		return initialTokenCount.equals(vother.initialTokenCount);
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ initialTokenCount.hashCode();
	}
}
