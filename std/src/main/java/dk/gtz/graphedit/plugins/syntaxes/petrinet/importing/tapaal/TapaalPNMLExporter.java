package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dk.gtz.graphedit.exceptions.ExportException;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelTransition;
import dk.gtz.graphedit.spi.IExporter;

public class TapaalPNMLExporter implements IExporter {
    private final static Logger logger = LoggerFactory.getLogger(TapaalPNMLExporter.class);
    private final XmlMapper xmlMapper;
    private final String syntaxName;

    public TapaalPNMLExporter(String syntaxName) {
	this.syntaxName = syntaxName;
	this.xmlMapper = new XmlMapper();
	this.xmlMapper.registerModule(new Jdk8Module());
    }

    @Override
    public String getName() {
	return "tapaal";
    }

    @Override
    public void exportFile(ModelProjectResource resource, Path newFilePath) throws ExportException {
	var syntaxName = Optional.ofNullable(resource.metadata().get("graphedit_syntax"));
	if(!syntaxName.isPresent())
	    throw new ExportException("No syntax specified for resource");
	if(!syntaxName.get().equals(this.syntaxName))
	    throw new ExportException("Invalid syntax for TapaalPNMLExporter '%s' (expected '%s')".formatted(syntaxName.get(), syntaxName));
	var pnml = fromResource(resource);
	try {
	    xmlMapper.writeValue(newFilePath.toFile(), pnml);
	} catch (IOException e) {
	    throw new ExportException("Failed to write PNML to file", e);
	}
    }

    private PNML fromResource(ModelProjectResource resource) throws ExportException {
	var name = Optional.ofNullable(resource.metadata().get("name"));
	if(name.isEmpty())
	    throw new ExportException("No name metadata specified for exported resource");

	var kbound = new KBound().setBound(0);
	var feature = new Feature().setGame(false).setTimed(false);
	var net = new Net()
	    .setActive(true)
	    .setId(name.get())
	    .setType("P/T net");

	var places = new ArrayList<Place>();
	var transitions = new ArrayList<Transition>();
	for(var vertex : resource.syntax().vertices().entrySet()) {
	    if(vertex.getValue() instanceof ModelPlace modelPlace) {
		places.add(new Place()
			.setId(vertex.getKey().toString())
			.setDisplayName(false)
			.setInvariant("")
			.setName("")
			.setNameOffsetX(0)
			.setNameOffsetY(0)
			.setPositionX(vertex.getValue().position.x())
			.setPositionY(vertex.getValue().position.y())
			.setInitialMarking(modelPlace.initialTokenCount()));
		continue;
	    }
	    if(vertex.getValue() instanceof ModelTransition) {
		transitions.add(new Transition()
			.setId(vertex.getKey().toString())
			.setAngle(0)
			.setDisplayName(false)
			.setInfiniteServer(false) // TODO: what is this? and is it required for regular P/N nets?
			.setName("")
			.setNameOffsetX(0)
			.setNameOffsetY(0)
			.setPlayer(0) // TODO: what is this? and is it required for regular P/N nets?
			.setPositionX(vertex.getValue().position.x())
			.setPositionY(vertex.getValue().position.y())
			.setPriority(0)
			.setUrgent(false));
		continue;
	    }
	    logger.warn("cannot export [vertex]({}): not a Place or Transition", vertex.getKey());
	}

	var arcs = new ArrayList<Arc>();
	for(var edge : resource.syntax().edges().entrySet()) {
	    if(edge.getValue() instanceof ModelArc modelArc) {
		arcs.add(new Arc()
			.setId(edge.getKey().toString())
			.setInscription("") // TODO: what is this? and is it required for regular P/N nets?
			.setNameOffsetX(0)
			.setNameOffsetY(0)
			.setSource(modelArc.source.toString())
			.setTarget(modelArc.target.toString())
			.setType("normal") // TODO: what is this? and is it required for regular P/N nets?
			.setWeight(modelArc.weight)
			.setArcpaths(new ArrayList<Arcpath>()));
		continue;
	    }
	    logger.warn("cannot export [edge]({}): not an Arc", edge.getKey());
	}

	net.setPlaces(places)
	    .setTransitions(transitions)
	    .setArcs(arcs);
	return new PNML()
	    .setkBound(kbound)
	    .setFeature(feature)
	    .setNets(new ArrayList<Net>(List.of(net)));
    }
}
