package dk.gtz.graphedit.plugins.syntaxes.lts.lsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.ModelLintSeverity;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.util.MetadataUtils;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelDiff;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yafunc.IRunnable1;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;

public class LTSLanguageServer implements ILanguageServer {
    private List<IRunnable1<Collection<ModelLint>>> diagnosticsHandlers;
    private List<IRunnable1<ModelNotification>> notificationHandlers;
    private List<IRunnable1<ModelLanguageServerProgress>> progressHandlers;
    private IBufferContainer bufferContainer;

    public LTSLanguageServer() {
	diagnosticsHandlers = new ArrayList<>();
	notificationHandlers = new ArrayList<>();
	progressHandlers = new ArrayList<>();
    }

    @Override
    public String getLanguageName() {
	return "LTS";
    }

    @Override
    public String getServerName() {
	return "lts-ls";
    }

    @Override
    public String getServerVersion() {
	return "v1.0.0";
    }

    @Override
    public void initialize(File projectFile, IBufferContainer bufferContainer) {
	final var ltsSyntax = MetadataUtils.getSyntaxFactory(getLanguageName());
	this.bufferContainer = bufferContainer;
	this.bufferContainer.getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
	    if(c.wasAdded()) {
		var changedVal = c.getValueAdded();
		var old = new SimpleObjectProperty<>(changedVal.toModel());
		changedVal.addListener((e,o,n) -> {
		    var syntaxName = n.getSyntaxName();
		    if(syntaxName.isEmpty())
			return;
		    if(!syntaxName.get().equals("LTS"))
			return;
		    var diff = ViewModelDiff.compare(new ViewModelProjectResource(old.get(), ltsSyntax), n);
		    old.set(n.toModel());
		    if(diff.getEdgeAdditions().isEmpty() && diff.getEdgeDeletions().isEmpty())
			return;
		    var cpy = ViewModelDiff.applyCopy(changedVal, diff);
		    broadcastDiagnostics(getSccs(c.getKey(), cpy));
		});
	    }
	});
    }

    private Collection<ModelLint> getSccs(String model, ViewModelProjectResource cpy) {
	var sccs = Tarjan.getStronglyConnectedComponents(getGraph(cpy.syntax())).stream().filter(a -> a.size() > 1).toList();
	var newLints = new ArrayList<ModelLint>(sccs.size());
	for(var scc : sccs)
	    newLints.add(new ModelLint(
		    model,
		    "LTS-E001",
		    ModelLintSeverity.WARNING,
		    "Strongly Connected Component",
		    "Potential looping can occur in these vertices",
		    Optional.empty(),
		    scc,
		    List.of()));
	return newLints;
    }

    private Map<UUID, Collection<UUID>> getGraph(ViewModelGraph graph) {
	var result = new HashMap<UUID,Collection<UUID>>();
	for(var vertex : graph.vertices().keySet())
	    result.put(vertex, new ArrayList<>());
	for(var edge : graph.edges().entrySet()) {
	    var source = edge.getValue().source().get();
	    var target = edge.getValue().target().get();
	    if(!result.containsKey(target))
		continue; // e.g. if the target is the mouse-tracker
	    if(!result.containsKey(source))
		result.put(source, new ArrayList<>());
	    result.get(source).add(target);
	}
	return result;
    }

    @Override
    public void start() {
	for(var buffer : bufferContainer.getBuffers().entrySet())
	    broadcastDiagnostics(getSccs(buffer.getKey(), buffer.getValue()));
	while(!Thread.interrupted()) {
	    try {
		Thread.sleep(1000);
	    } catch (InterruptedException e) {
		return;
	    }
	}
    }

    @Override
    public Collection<ModelLint> getDiagnostics() {
	return List.of();
    }

    @Override
    public void addDiagnosticsCallback(IRunnable1<Collection<ModelLint>> callback) {
	diagnosticsHandlers.add(callback);
    }

    @Override
    public void addNotificationCallback(IRunnable1<ModelNotification> callback) {
	notificationHandlers.add(callback);
    }

    @Override
    public void addProgressCallback(IRunnable1<ModelLanguageServerProgress> callback) {
	progressHandlers.add(callback);
    }

    private void broadcastDiagnostics(Collection<ModelLint> lints) {
	diagnosticsHandlers.forEach(e -> e.run(lints));
    }

    private void broadcastProgress(ModelLanguageServerProgress report) {
	progressHandlers.forEach(e -> e.run(report));
    }

    private void broadcastNotification(ModelNotification notification) {
	notificationHandlers.forEach(e -> e.run(notification));
    }
}
