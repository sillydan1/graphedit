package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Place {
    @JacksonXmlProperty(isAttribute = true)
    private boolean displayName;
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private int initialMarking;
    @JacksonXmlProperty(isAttribute = true)
    private String invariant;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private double nameOffsetX;
    @JacksonXmlProperty(isAttribute = true)
    private double nameOffsetY;
    @JacksonXmlProperty(isAttribute = true)
    private double positionX;
    @JacksonXmlProperty(isAttribute = true)
    private double positionY;

    public boolean isDisplayName() {
	return displayName;
    }

    public void setDisplayName(boolean displayName) {
	this.displayName = displayName;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public int getInitialMarking() {
	return initialMarking;
    }

    public void setInitialMarking(int initialMarking) {
	this.initialMarking = initialMarking;
    }

    public String getInvariant() {
	return invariant;
    }

    public void setInvariant(String invariant) {
	this.invariant = invariant;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public double getNameOffsetX() {
	return nameOffsetX;
    }

    public void setNameOffsetX(double nameOffsetX) {
	this.nameOffsetX = nameOffsetX;
    }

    public double getNameOffsetY() {
	return nameOffsetY;
    }

    public void setNameOffsetY(double nameOffsetY) {
	this.nameOffsetY = nameOffsetY;
    }

    public double getPositionX() {
	return positionX;
    }

    public void setPositionX(double positionX) {
	this.positionX = positionX;
    }

    public double getPositionY() {
	return positionY;
    }

    public void setPositionY(double positionY) {
	this.positionY = positionY;
    }
}
