package dk.gtz.graphedit.syntaxes.lts.model;

import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelVertex;

public class ModelState extends ModelVertex {
    private String name;
    private boolean isInitial;

    public String name() {
        return name;
    }

    public boolean isInitial() {
        return isInitial;
    }

    public ModelState() {
        this(new ModelPoint(0,0), "", false);
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
        this.isInitial = isInitial;
    }
}

