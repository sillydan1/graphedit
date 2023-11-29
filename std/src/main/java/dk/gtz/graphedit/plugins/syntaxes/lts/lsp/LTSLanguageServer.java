package dk.gtz.graphedit.plugins.syntaxes.lts.lsp;

import java.io.File;
import java.util.Collection;
import java.util.List;

import dk.gtz.graphedit.model.ModelLint;
import dk.gtz.graphedit.model.lsp.ModelLanguageServerProgress;
import dk.gtz.graphedit.model.lsp.ModelNotification;
import dk.gtz.graphedit.spi.ILanguageServer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.yalibs.yafunc.IRunnable1;
import javafx.collections.MapChangeListener;

public class LTSLanguageServer implements ILanguageServer {
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
	bufferContainer.getBuffers().addListener((MapChangeListener<String,ViewModelProjectResource>)c -> {
	    var changedKey = c.getKey();
	    // TODO: add change listener on all open graphs (applyCopy diffs) that checks for SCCs using Tarjan
	});
    }

    @Override
    public void start() {
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
    }

    @Override
    public void addNotificationCallback(IRunnable1<ModelNotification> callback) {
    }

    @Override
    public void addProgressCallback(IRunnable1<ModelLanguageServerProgress> callback) {
    }
}
