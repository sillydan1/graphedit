package dk.gtz.graphedit.model;

import java.awt.Point;

/**
 * The base class of a graph vertex.
 */
public class Vertex {
    private final Point position;

    public Vertex(Point position) {
	this.position = position;
    }

    public Vertex() {
	this(new Point());
    }

    public Point getPosition() {
	return position;
    }
}

