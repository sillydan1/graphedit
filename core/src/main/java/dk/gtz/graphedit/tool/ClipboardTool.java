package dk.gtz.graphedit.tool;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.gtz.graphedit.events.ViewportKeyEvent;
import dk.gtz.graphedit.exceptions.SerializationException;
import dk.gtz.graphedit.exceptions.UncomparableException;
import dk.gtz.graphedit.model.ModelGraph;
import dk.gtz.graphedit.serialization.IModelSerializer;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.ViewModelDiff;
import dk.gtz.graphedit.viewmodel.ViewModelEdge;
import dk.gtz.graphedit.viewmodel.ViewModelGraph;
import dk.gtz.graphedit.viewmodel.ViewModelPoint;
import dk.gtz.graphedit.viewmodel.ViewModelProjectResource;
import dk.gtz.graphedit.viewmodel.ViewModelSelection;
import dk.gtz.graphedit.viewmodel.ViewModelVertex;
import dk.yalibs.yadi.DI;
import dk.yalibs.yaundo.IUndoSystem;
import dk.yalibs.yaundo.Undoable;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Tool that enables cut/copy/paste support
 *
 * - ctrl + x to cut
 * - ctrl + c to copy
 * - ctrl + v to paste
 */
public class ClipboardTool extends AbstractBaseTool {
    private final Logger logger = LoggerFactory.getLogger(ClipboardTool.class);
    private final ObservableList<ViewModelSelection> selectedElements;
    private final IModelSerializer serializer;
    private final IBufferContainer buffers;
    private final Clipboard clipboard;
    private final IUndoSystem undoSystem;
    private final MassDeleteTool deleteTool;

    public ClipboardTool() {
        selectedElements = DI.get("selectedElements");
        serializer = DI.get(IModelSerializer.class);
        buffers = DI.get(IBufferContainer.class);
        undoSystem = DI.get(IUndoSystem.class);
        clipboard = Clipboard.getSystemClipboard();
        deleteTool = new MassDeleteTool();
    }

    @Override
    public String getHelpDescription() {
        return """
            Tool that enables cut/copy/paste support

             - <Shortcut> + X to cut
             - <Shortcut> + C to copy
             - <Shortcut> + V to paste
            """;
    }

    @Override
    public Optional<String> getTooltip() {
        return Optional.of("copy and paste sections of models");
    }

    @Override
    public Node getGraphic() {
        return new FontIcon(BootstrapIcons.CLIPBOARD);
    }

    @Override
    public void onKeyEvent(ViewportKeyEvent e) {
        if(!e.event().isShortcutDown())
            return;
        if(!e.event().getEventType().equals(KeyEvent.KEY_PRESSED))
            return;
        if(e.event().getCode().equals(KeyCode.C))
            copySelection(e);
        if(e.event().getCode().equals(KeyCode.V))
            pasteModel(e);
        if(e.event().getCode().equals(KeyCode.X))
            cutSelection(e);
    }

    public void copySelection(ViewportKeyEvent e) {
        try {
            if(selectedElements.isEmpty())
                return;
            var buffer = buffers.get(e.bufferId());
            var selectedVertices = buffer.syntax().vertices().entrySet().stream()
                    .filter(elem -> selectedElements.stream().anyMatch(s -> s.id().equals(elem.getKey())))
                    .collect(Collectors.toMap(
                                elem -> elem.getKey(),
                                elem -> elem.getValue().toModel()));
            var selectedEdges = buffer.syntax().edges().entrySet().stream()
                    .filter(elem -> selectedElements.stream().anyMatch(s -> s.id().equals(elem.getKey())))
                    .collect(Collectors.toMap(
                                elem -> elem.getKey(),
                                elem -> elem.getValue().toModel()));
            for(var edge : selectedEdges.entrySet()) {
                if(!selectedVertices.containsKey(edge.getValue().source))
                    selectedVertices.put(edge.getValue().source, buffer.syntax().vertices().get(edge.getValue().source).toModel());
                if(!selectedVertices.containsKey(edge.getValue().target))
                    selectedVertices.put(edge.getValue().target, buffer.syntax().vertices().get(edge.getValue().target).toModel());
            }
            var modelGraph = new ModelGraph("", selectedVertices, selectedEdges); // NOTE: Declarations string cannot be copied by this tool
            var resource = new ViewModelProjectResource(buffer.metadata(), new ViewModelGraph(modelGraph, e.syntax())).toModel();
            var serializedModel = serializer.serialize(resource);
            var content = new ClipboardContent();
            content.putString(serializedModel);
            clipboard.setContent(content);
            logger.trace("copied {} vertices and {} edges to clipboard", selectedVertices.size(), selectedEdges.size());
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public void pasteModel(ViewportKeyEvent e) {
        try {
            if(!clipboard.hasString()) {
                logger.error("no plain text in clipboard");
                return;
            }
            var syntaxFactory = e.syntax();
            var buffer = buffers.get(e.bufferId());
            var content = clipboard.getString();
            var resource = new ViewModelProjectResource(serializer.deserializeProjectResource(content), e.syntax());
            var vertexOffset = new ViewModelPoint(e.editorSettings().gridSizeX().get(), e.editorSettings().gridSizeY().get());
            var rerolledVertex = new HashMap<UUID,ViewModelVertex>();
            var rerolledEdges = new HashMap<UUID,ViewModelEdge>();
            var remapping = new HashMap<UUID,UUID>();
            for(var vertex : resource.syntax().vertices().entrySet()) {
                var v = vertex.getValue();
                var rerolled = UUID.randomUUID();
                v.position().setValue(v.position().add(vertexOffset));
                rerolledVertex.put(rerolled, syntaxFactory.createVertexViewModel(rerolled, v.toModel()));
                remapping.put(vertex.getKey(), rerolled);
            }
            for(var edge : resource.syntax().edges().entrySet()) {
                var ev = edge.getValue();
                var rerolled = UUID.randomUUID();
                ev.source().set(remapping.get(ev.source().get()));
                ev.target().set(remapping.get(ev.target().get()));
                assert ev.source().get() != null;
                assert ev.target().get() != null;
                rerolledEdges.put(rerolled, syntaxFactory.createEdgeViewModel(rerolled, ev.toModel()));
            }
            resource.syntax().vertices().clear();
            resource.syntax().vertices().putAll(rerolledVertex);
            resource.syntax().edges().clear();
            resource.syntax().edges().putAll(rerolledEdges);
            var diff = ViewModelDiff.compare(buffer, resource);
            ViewModelDiff.applyAdditiveOnly(buffer, diff);
            undoSystem.push(new Undoable("paste content",
                        () -> ViewModelDiff.revertAdditiveOnly(buffer, diff),
                        () -> ViewModelDiff.applyAdditiveOnly(buffer, diff)));
        } catch (SerializationException exc) {
            logger.trace("clipboard value not deserializable {}", exc.getMessage());
        } catch (UncomparableException exc) {
            logger.warn("clipboard model and target model are different syntaxes");
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public void cutSelection(ViewportKeyEvent e) {
        try {
            if(selectedElements.isEmpty())
                return;
            copySelection(e);
            deleteTool.deleteSelectedElements(e);
        } catch(Exception exc) {
            throw new RuntimeException(exc);
        }
    }
}
