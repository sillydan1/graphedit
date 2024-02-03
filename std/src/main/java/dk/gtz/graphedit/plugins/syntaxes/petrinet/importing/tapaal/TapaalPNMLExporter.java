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
import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelArc;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelPlace;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.viewmodel.ViewModelTransition;
import dk.gtz.graphedit.spi.IExporter;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;

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
    public void exportFile(ViewModelProjectResource resource, Path newFilePath) throws ExportException {
	if(!resource.getSyntaxName().isPresent())
	    throw new ExportException("No syntax specified for exported resource");
	if(!resource.getSyntaxName().get().equals(syntaxName))
	    throw new ExportException("Invalid syntax for TapaalPNMLExporter '%s' (expected '%s')".formatted(resource.getSyntaxName().get(), syntaxName));
	var pnml = fromResource(resource);
	try {
	    xmlMapper.writeValue(newFilePath.toFile(), pnml);
	} catch (IOException e) {
	    throw new ExportException("Failed to write PNML to file", e);
	}
    }

    private PNML fromResource(ViewModelProjectResource resource) throws ExportException {
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
	    if(vertex.getValue() instanceof ViewModelPlace modelPlace) {
		places.add(new Place()
			.setId(vertex.getKey().toString())
			.setDisplayName(false)
			.setInvariant("")
			.setName("")
			.setNameOffsetX(0)
			.setNameOffsetY(0)
			.setPositionX(vertex.getValue().position().getX())
			.setPositionY(vertex.getValue().position().getY())
			.setInitialMarking(modelPlace.initialTokenCount().get()));
		continue;
	    }
	    if(vertex.getValue() instanceof ViewModelTransition) {
		transitions.add(new Transition()
			.setId(vertex.getKey().toString())
			.setAngle(0)
			.setDisplayName(false)
			.setInfiniteServer(false) // TODO: what is this? and is it required for regular P/N nets?
			.setName("")
			.setNameOffsetX(0)
			.setNameOffsetY(0)
			.setPlayer(0) // TODO: what is this? and is it required for regular P/N nets?
			.setPositionX(vertex.getValue().position().getX())
			.setPositionY(vertex.getValue().position().getY())
			.setPriority(0)
			.setUrgent(false));
		continue;
	    }
	    logger.warn("cannot export [vertex]({}): not a Place or Transition", vertex.getKey());
	}

	var arcs = new ArrayList<Arc>();
	for(var edge : resource.syntax().edges().entrySet()) {
	    if(edge.getValue() instanceof ViewModelArc modelArc) {
		arcs.add(new Arc()
			.setId(edge.getKey().toString())
			.setInscription("") // TODO: what is this? and is it required for regular P/N nets?
			.setNameOffsetX(0)
			.setNameOffsetY(0)
			.setSource(modelArc.source().toString())
			.setTarget(modelArc.target().toString())
			.setType("normal") // TODO: what is this? and is it required for regular P/N nets?
			.setWeight(modelArc.weight().get())
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
