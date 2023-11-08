package dk.gtz.graphedit.plugins.syntaxes.text.model;

import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelVertex;

public class ModelTextVertex extends ModelVertex {
	public String label;

	public String label() {
		return label;
	}

	public ModelTextVertex() {
		super();
		this.label = "";
	}

	public ModelTextVertex(ModelPoint position, String label) {
		super(position);
		this.label = label;
	}
}

