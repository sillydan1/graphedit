package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Transition {
    @JacksonXmlProperty(isAttribute = true)
    private double angle;
    @JacksonXmlProperty(isAttribute = true)
    private boolean displayName;
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private boolean infiniteServer;
    @JacksonXmlProperty(isAttribute = true)
    private String name;
    @JacksonXmlProperty(isAttribute = true)
    private double nameOffsetX;
    @JacksonXmlProperty(isAttribute = true)
    private double nameOffsetY;
    @JacksonXmlProperty(isAttribute = true)
    private int player;
    @JacksonXmlProperty(isAttribute = true)
    private double positionX;
    @JacksonXmlProperty(isAttribute = true)
    private double positionY;
    @JacksonXmlProperty(isAttribute = true)
    private int priority;
    @JacksonXmlProperty(isAttribute = true)
    private boolean urgent;

    public double getAngle() {
	return angle;
    }

    public void setAngle(double angle) {
	this.angle = angle;
    }

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

    public boolean isInfiniteServer() {
	return infiniteServer;
    }

    public void setInfiniteServer(boolean infiniteServer) {
	this.infiniteServer = infiniteServer;
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

    public int getPlayer() {
	return player;
    }

    public void setPlayer(int player) {
	this.player = player;
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

    public int getPriority() {
	return priority;
    }

    public void setPriority(int priority) {
	this.priority = priority;
    }

    public boolean isUrgent() {
	return urgent;
    }

    public void setUrgent(boolean urgent) {
	this.urgent = urgent;
    }
}
