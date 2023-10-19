package dk.gtz.graphedit.syntaxes.lts.viewmodel;

import java.util.ArrayList;
import java.util.List;

import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.syntaxes.lts.model.ModelState;
import dk.gtz.graphedit.viewmodel.ISearchable;
import dk.gtz.graphedit.viewmodel.InspectableProperty;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ViewModelState extends ViewModelVertex implements ISearchable {
	private final StringProperty name;
	private final BooleanProperty initial;

	public StringProperty name() {
		return name;
	}

	public BooleanProperty initial() {
		return initial;
	}

	public ViewModelState(ModelVertex vertex) {
		super(vertex);
		name = new SimpleStringProperty("");
		initial = new SimpleBooleanProperty(false);
		if(vertex instanceof ModelState tvertex) {
			this.name.set(tvertex.name());
			this.initial.set(tvertex.isInitial());
		}
	}

	public ViewModelState(ModelState vertex) {
		super(vertex);
		name = new SimpleStringProperty(vertex.name());
		initial = new SimpleBooleanProperty(vertex.isInitial());
	}

	public ViewModelState(ViewModelVertex vertex) {
		super(vertex.position(), vertex.shape());
		name = new SimpleStringProperty("");
		initial = new SimpleBooleanProperty(false);
	}

	/**
	 * {@inheritDoc}
	 * @return A list of inspectable objects
	 * */
	@Override
	public List<InspectableProperty> getInspectableObjects() {
		var inspectables = new ArrayList<>(super.getInspectableObjects());
		inspectables.add(new InspectableProperty("Initial", initial));
		inspectables.add(new InspectableProperty("Label", name));
		return inspectables;
	}

	@Override
	public List<String> getSearchValues() {
		var result = new ArrayList<>(List.of(name.getValueSafe()));
		if(initial.get())
			result.add("initial");
		return result;
	}

	@Override
	public ModelVertex toModel() {
		return new ModelState(position().toModel(), name.getValueSafe(), initial.get());
	}

	@Override
	public void addListener(ChangeListener<? super ViewModelVertex> listener) {
		super.addListener(listener);
		name.addListener((e,o,n) -> listener.changed(this,this,this));
		initial.removeListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void removeListener(ChangeListener<? super ViewModelVertex> listener) {
		super.addListener(listener);
		name.removeListener((e,o,n) -> listener.changed(this,this,this));
		initial.removeListener((e,o,n) -> listener.changed(this,this,this));
	}

	@Override
	public void addListener(InvalidationListener listener) {
		super.addListener(listener);
		name.addListener(listener);
		initial.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		super.addListener(listener);
		name.removeListener(listener);
		initial.removeListener(listener);
	}

	@Override
	public void setValue(ViewModelVertex value) {
		super.setValue(value);
		if(value instanceof ViewModelState tvalue) {
			name.setValue(tvalue.name().get());
			initial.setValue(tvalue.initial().get());
		}
	}

	@Override
	public void bind(ObservableValue<? extends ViewModelVertex> observable) {
		super.bind(observable);
		if(observable.getValue() instanceof ViewModelState tobs) {
			name.bind(tobs.name());
			initial.bind(tobs.initial());
		}
	}

	@Override
	public void unbind() {
		super.unbind();
		name.unbind();
		initial.unbind();
	}

	@Override
	public boolean isBound() {
		return super.isBound() || name.isBound() || initial.isBound();
	}

	@Override
	public void bindBidirectional(Property<ViewModelVertex> other) {
		super.bindBidirectional(other);
		if(other.getValue() instanceof ViewModelState tother) {
			name.bindBidirectional(tother.name());
			initial.bindBidirectional(tother.initial());
		}
	}

	@Override
	public void unbindBidirectional(Property<ViewModelVertex> other) {
		super.unbindBidirectional(other);
		if(other.getValue() instanceof ViewModelState tother) {
			name.unbindBidirectional(tother.name());
			initial.unbindBidirectional(tother.initial());
		}
	}
}

