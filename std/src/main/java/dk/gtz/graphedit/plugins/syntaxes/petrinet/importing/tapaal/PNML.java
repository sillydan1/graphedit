package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "pnml", namespace = "http://www.informatik.hu-berlin.de/top/pnml/ptNetb")
public class PNML {
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "net")
	private List<Net> nets;
	@JacksonXmlProperty(localName = "k-bound")
	private KBound kBound;
	@JacksonXmlProperty(localName = "feature")
	private Feature feature;

	public List<Net> getNets() {
		return nets;
	}

	public PNML setNets(List<Net> net) {
		this.nets = net;
		return this;
	}

	public KBound getkBound() {
		return kBound;
	}
	
	public PNML setkBound(KBound kBound) {
		this.kBound = kBound;
		return this;
	}

	public Feature getFeature() {
		return feature;
	}

	public PNML setFeature(Feature feature) {
		this.feature = feature;
		return this;
	}
}
