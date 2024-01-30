package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Arc {
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private String inscription;
    @JacksonXmlProperty(isAttribute = true)
    private double nameOffsetX;
    @JacksonXmlProperty(isAttribute = true)
    private double nameOffsetY;
    @JacksonXmlProperty(isAttribute = true)
    private String source;
    @JacksonXmlProperty(isAttribute = true)
    private String target;
    @JacksonXmlProperty(isAttribute = true)
    private String type;
    @JacksonXmlProperty(isAttribute = true)
    private int weight;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "arcpath")
    private List<Arcpath> arcpaths;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getInscription() {
	return inscription;
    }

    public void setInscription(String inscription) {
	this.inscription = inscription;
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

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    public String getTarget() {
	return target;
    }

    public void setTarget(String target) {
	this.target = target;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public int getWeight() {
	return weight;
    }

    public void setWeight(int weight) {
	this.weight = weight;
    }

    public List<Arcpath> getArcpaths() {
	return arcpaths;
    }

    public void setArcpaths(List<Arcpath> argpaths) {
	this.arcpaths = argpaths;
    }
}
