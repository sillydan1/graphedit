package dk.gtz.graphedit.viewmodel;

import javafx.beans.Observable;

public record InspectableProperty(String name, Observable property) {}

