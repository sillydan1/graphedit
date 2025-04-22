package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transition {
	@JacksonXmlProperty(isAttribute = true)
	private int angle;
	@JacksonXmlProperty(isAttribute = true)
	private boolean displayName;
	@JacksonXmlProperty(isAttribute = true)
	private String id;
	@JacksonXmlProperty(isAttribute = true)
	private boolean infiniteServer;
	@JacksonXmlProperty(isAttribute = true)
	private String name;
	@JacksonXmlProperty(isAttribute = true)
	private double nameOffsetX;
	@JacksonXmlProperty(isAttribute = true)
	private double nameOffsetY;
	@JacksonXmlProperty(isAttribute = true)
	private int player;
	@JacksonXmlProperty(isAttribute = true)
	private double positionX;
	@JacksonXmlProperty(isAttribute = true)
	private double positionY;
	@JacksonXmlProperty(isAttribute = true)
	private int priority;
	@JacksonXmlProperty(isAttribute = true)
	private boolean urgent;

	public int getAngle() {
		return angle;
	}

	public Transition setAngle(int angle) {
		this.angle = angle;
		return this;
	}

	public boolean isDisplayName() {
		return displayName;
	}

	public Transition setDisplayName(boolean displayName) {
		this.displayName = displayName;
		return this;
	}

	public String getId() {
		return id;
	}

	public Transition setId(String id) {
		this.id = id;
		return this;
	}

	public boolean isInfiniteServer() {
		return infiniteServer;
	}

	public Transition setInfiniteServer(boolean infiniteServer) {
		this.infiniteServer = infiniteServer;
		return this;
	}

	public String getName() {
		return name;
	}

	public Transition setName(String name) {
		this.name = name;
		return this;
	}

	public double getNameOffsetX() {
		return nameOffsetX;
	}

	public Transition setNameOffsetX(double nameOffsetX) {
		this.nameOffsetX = nameOffsetX;
		return this;
	}

	public double getNameOffsetY() {
		return nameOffsetY;
	}

	public Transition setNameOffsetY(double nameOffsetY) {
		this.nameOffsetY = nameOffsetY;
		return this;
	}

	public int getPlayer() {
		return player;
	}

	public Transition setPlayer(int player) {
		this.player = player;
		return this;
	}

	public double getPositionX() {
		return positionX;
	}

	public Transition setPositionX(double positionX) {
		this.positionX = positionX;
		return this;
	}

	public double getPositionY() {
		return positionY;
	}

	public Transition setPositionY(double positionY) {
		this.positionY = positionY;
		return this;
	}

	public int getPriority() {
		return priority;
	}

	public Transition setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public boolean isUrgent() {
		return urgent;
	}

	public Transition setUrgent(boolean urgent) {
		this.urgent = urgent;
		return this;
	}
}
