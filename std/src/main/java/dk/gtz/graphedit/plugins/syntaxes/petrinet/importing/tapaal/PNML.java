package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "pnml", namespace = "http://www.informatik.hu-berlin.de/top/pnml/ptNetb")
public class PNML {
	@JacksonXmlProperty(localName = "net")
	private Net net;
	@JacksonXmlProperty(localName = "k-bound")
	private KBound kBound;
	@JacksonXmlProperty(localName = "feature")
	private Feature feature;

	public Net getNet() {
		return net;
	}

	public void setNet(Net net) {
		this.net = net;
	}

	public KBound getKBound() {
		return kBound;
	}

	public void setKBound(KBound kBound) {
		this.kBound = kBound;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}
}
