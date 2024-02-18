package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public Arc setId(String id) {
	this.id = id;
	return this;
    }

    public String getInscription() {
	return inscription;
    }

    public Arc setInscription(String inscription) {
	this.inscription = inscription;
	return this;
    }

    public double getNameOffsetX() {
	return nameOffsetX;
    }

    public Arc setNameOffsetX(double nameOffsetX) {
	this.nameOffsetX = nameOffsetX;
	return this;
    }

    public double getNameOffsetY() {
	return nameOffsetY;
    }

    public Arc setNameOffsetY(double nameOffsetY) {
	this.nameOffsetY = nameOffsetY;
	return this;
    }

    public String getSource() {
	return source;
    }

    public Arc setSource(String source) {
	this.source = source;
	return this;
    }

    public String getTarget() {
	return target;
    }

    public Arc setTarget(String target) {
	this.target = target;
	return this;
    }

    public String getType() {
	return type;
    }

    public Arc setType(String type) {
	this.type = type;
	return this;
    }

    public int getWeight() {
	return weight;
    }

    public Arc setWeight(int weight) {
	this.weight = weight;
	return this;
    }

    public List<Arcpath> getArcpaths() {
	return arcpaths;
    }

    public Arc setArcpaths(List<Arcpath> argpaths) {
	this.arcpaths = argpaths;
	return this;
    }
}
