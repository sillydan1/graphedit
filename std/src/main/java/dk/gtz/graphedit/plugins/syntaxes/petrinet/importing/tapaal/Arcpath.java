package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Arcpath {
    @JacksonXmlProperty(isAttribute = true)
    private boolean arcPointType;
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private double xCoord;
    @JacksonXmlProperty(isAttribute = true)
    private double yCoord;

    public boolean getArcPointType() {
	return arcPointType;
    }

    public void setArcPointType(boolean arcPointType) {
	this.arcPointType = arcPointType;
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public double getXCoord() {
	return xCoord;
    }

    public void setXCoord(double xCoord) {
	this.xCoord = xCoord;
    }

    public double getYCoord() {
	return yCoord;
    }

    public void setYCoord(double yCoord) {
	this.yCoord = yCoord;
    }
}
