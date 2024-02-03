package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


public class Feature {
    @JacksonXmlProperty(isAttribute = true)
    private boolean isGame;
    @JacksonXmlProperty(isAttribute = true)
    private boolean isTimed;

    public boolean isGame() {
	return isGame;
    }

    public Feature setGame(boolean isGame) {
	this.isGame = isGame;
	return this;
    }

    public boolean isTimed() {
	return isTimed;
    }

    public Feature setTimed(boolean isTimed) {
	this.isTimed = isTimed;
	return this;
    }
}
