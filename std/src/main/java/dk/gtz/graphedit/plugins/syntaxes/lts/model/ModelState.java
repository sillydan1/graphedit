package dk.gtz.graphedit.plugins.syntaxes.lts.model;

import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelVertex;

public class ModelState extends ModelVertex {
	public String name;
	public boolean initial;

	public String name() {
		return name;
	}

	public boolean isInitial() {
		return initial;
	}

	public ModelState() {
		this(new ModelPoint(0, 0), "", false);
	}

	public ModelState(ModelPoint position, boolean isInitial) {
		this(position, "", isInitial);
	}

	public ModelState(ModelPoint position, String name) {
		this(position, name, false);
	}

	public ModelState(ModelPoint position, String name, boolean isInitial) {
		super(position);
		this.name = name;
		this.initial = isInitial;
	}
}
