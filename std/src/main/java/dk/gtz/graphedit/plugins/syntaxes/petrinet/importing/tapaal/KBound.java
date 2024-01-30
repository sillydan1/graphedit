package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class KBound {
    @JacksonXmlProperty(isAttribute = true)
    private int bound;

    public int getBound() {
	return bound;
    }

    public void setBound(int bound) {
	this.bound = bound;
    }
}
