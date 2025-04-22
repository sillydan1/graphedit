package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KBound {
	@JacksonXmlProperty(isAttribute = true)
	private int bound;

	public int getBound() {
		return bound;
	}

	public KBound setBound(int bound) {
		this.bound = bound;
		return this;
	}
}
