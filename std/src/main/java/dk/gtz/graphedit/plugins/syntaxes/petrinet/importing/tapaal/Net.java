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

	public Net setActive(boolean active) {
		this.active = active;
		return this;
	}

	public String getId() {
		return id;
	}

	public Net setId(String id) {
		this.id = id;
		return this;
	}

	public String getType() {
		return type;
	}

	public Net setType(String type) {
		this.type = type;
		return this;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public Net setPlaces(List<Place> places) {
		this.places = places;
		return this;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public Net setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
		return this;
	}

	public List<Arc> getArcs() {
		return arcs;
	}

	public Net setArcs(List<Arc> arcs) {
		this.arcs = arcs;
		return this;
	}
}
