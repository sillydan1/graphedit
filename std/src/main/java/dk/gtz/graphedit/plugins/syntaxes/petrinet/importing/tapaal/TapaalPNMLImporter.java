package dk.gtz.graphedit.plugins.syntaxes.petrinet.importing.tapaal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dk.gtz.graphedit.exceptions.ImportException;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.model.ModelPoint;
import dk.gtz.graphedit.model.ModelProjectResource;
import dk.gtz.graphedit.model.ModelVertex;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelArc;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelPlace;
import dk.gtz.graphedit.plugins.syntaxes.petrinet.model.ModelTransition;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.spi.IImporter;
import dk.gtz.graphedit.viewmodel.ViewModelProject;
import dk.yalibs.yadi.DI;

public class TapaalPNMLImporter implements IImporter {
	private static final Logger logger = LoggerFactory.getLogger(TapaalPNMLImporter.class);
	private final XmlMapper xmlMapper;
	private final String syntaxName;

	public TapaalPNMLImporter(String syntaxName) {
		this.syntaxName = syntaxName;
		this.xmlMapper = new XmlMapper();
		this.xmlMapper.registerModule(new Jdk8Module());
	}

	@Override
	public String getName() {
		return "tapaal";
	}

	@Override
	public List<ImportResult> importFiles(List<File> importFiles) throws ImportException {
		var result = new ArrayList<ImportResult>();
		var preferedFileExtension = DI.get(IModelSerializer.class).getPreferedFileExtension();
		for(var importFile : importFiles) {
			try {
				var pnml = xmlMapper.readValue(importFile, PNML.class);
				var nets = pnml.getNets();
				for(var net : nets) {
					var projectPath = DI.get(ViewModelProject.class).rootDirectory().getValueSafe();
					var filename = net.getId() + preferedFileExtension;
					result.add(new ImportResult(Path.of(projectPath, filename), fromNet(net)));
				}
			} catch (IOException e) {
				throw new ImportException(e);
			}
		}
		return result;
	}

	private ModelProjectResource fromNet(Net net) {
		var vertexIdMapping = new HashMap<String,UUID>();
		var vertices = new HashMap<UUID,ModelVertex>();
		for(var place : net.getPlaces()) {
			var placeLocation = new ModelPoint(place.getPositionX(), place.getPositionY());
			var modelPlace = new ModelPlace(placeLocation, place.getInitialMarking());
			var newUUID = UUID.randomUUID();
			vertices.put(newUUID, modelPlace);
			vertexIdMapping.put(place.getId(), newUUID);
		}
		for(var transition : net.getTransitions()) {
			var transitionLocation = new ModelPoint(transition.getPositionX(), transition.getPositionY());
			var modelTransition = new ModelTransition(transitionLocation);
			var newUUID = UUID.randomUUID();
			vertices.put(newUUID, modelTransition);
			vertexIdMapping.put(transition.getId(), newUUID);
		}
		var edges = new HashMap<UUID,dk.gtz.graphedit.model.ModelEdge>();
		for(var arc : net.getArcs()) {
			var sourceUUID = vertexIdMapping.get(arc.getSource());
			var targetUUID = vertexIdMapping.get(arc.getTarget());
			var modelEdge = new ModelArc(sourceUUID, targetUUID);
			edges.put(UUID.randomUUID(), modelEdge);
		}
		var metadata = new HashMap<String,String>();
		metadata.put("graphedit_syntax", syntaxName);
		return new ModelProjectResource(metadata, new ModelGraph("", vertices, edges));
	}

	@Override
	public FiletypesFilter getFiletypesFilter() {
		return new FiletypesFilter("TAPN Files", List.of("*.tapn"));
	}
}
