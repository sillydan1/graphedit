package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Net {
	@JacksonXmlProperty(isAttribute = true)
	private boolean active;
	@JacksonXmlProperty(isAttribute = true)
	private String id;
	@JacksonXmlProperty(isAttribute = true)
	private String type;
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "place")
	private List<Place> places;
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "transition")
	private List<Transition> transitions;
	@JacksonXmlElementWrapper(useWrapping = false)
	@JacksonXmlProperty(localName = "arc")
	private List<Arc> arcs;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public List<Arc> getArcs() {
		return arcs;
	}

	public void setArcs(List<Arc> arcs) {
		this.arcs = arcs;
	}
}
