package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feature {
    @JacksonXmlProperty(isAttribute = true)
    private boolean isGame;
    @JacksonXmlProperty(isAttribute = true)
    private boolean isTimed;

    public boolean isGame() {
	return isGame;
    }

    public Feature setIsGame(boolean isGame) {
	this.isGame = isGame;
	return this;
    }

    public boolean isTimed() {
	return isTimed;
    }

    public Feature setIsTimed(boolean isTimed) {
	this.isTimed = isTimed;
	return this;
    }
}
