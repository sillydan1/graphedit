package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public Place setDisplayName(boolean displayName) {
	this.displayName = displayName;
	return this;
    }

    public String getId() {
	return id;
    }

    public Place setId(String id) {
	this.id = id;
	return this;
    }

    public int getInitialMarking() {
	return initialMarking;
    }

    public Place setInitialMarking(int initialMarking) {
	this.initialMarking = initialMarking;
	return this;
    }

    public String getInvariant() {
	return invariant;
    }

    public Place setInvariant(String invariant) {
	this.invariant = invariant;
	return this;
    }

    public String getName() {
	return name;
    }

    public Place setName(String name) {
	this.name = name;
	return this;
    }

    public double getNameOffsetX() {
	return nameOffsetX;
    }

    public Place setNameOffsetX(double nameOffsetX) {
	this.nameOffsetX = nameOffsetX;
	return this;
    }

    public double getNameOffsetY() {
	return nameOffsetY;
    }

    public Place setNameOffsetY(double nameOffsetY) {
	this.nameOffsetY = nameOffsetY;
	return this;
    }

    public double getPositionX() {
	return positionX;
    }

    public Place setPositionX(double positionX) {
	this.positionX = positionX;
	return this;
    }

    public double getPositionY() {
	return positionY;
    }

    public Place setPositionY(double positionY) {
	this.positionY = positionY;
	return this;
    }
}
