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

    public void setGame(boolean isGame) {
	this.isGame = isGame;
    }

    public boolean isTimed() {
	return isTimed;
    }

    public void setTimed(boolean isTimed) {
	this.isTimed = isTimed;
    }
}
