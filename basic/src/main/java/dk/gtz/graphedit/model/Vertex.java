package dk.gtz.graphedit.model;

import java.awt.Point;

public abstract class Vertex {
    private final Point position;

    public Vertex(Point position) {
	this.position = position;
    }

    public Point getPosition() {
	return position;
    }
}

