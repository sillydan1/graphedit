package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atlantafx.base.theme.Styles;
import dk.yalibs.yadi.DI;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * A draggable tab that can optionally be detached from its tab pane and shown
 * in a separate window. This can be added to any normal TabPane, however a
 * TabPane with draggable tabs must *only* have DraggableTabs, normal tabs and
 * DrragableTabs mixed will cause issues!
 *
 * Edits by Asger Gitz-Johansen: General code cleanup and make it easier to use
 * in an FXML context.
 * 
 * @author Michael Berry
 * @author Asger Gitz-Johansen
 */
public class DraggableTabController extends Tab implements IProjectResourceView {
	private record InsertData(int index, TabPane insertPane) {
	}

	private static final Set<TabPane> tabPanes = new HashSet<>();
	private Label nameLabel;
	private Text dragText;
	private static final Stage markerStage;
	private Stage dragStage, newStage;
	private boolean detachable;
	private final List<EventHandler<WindowEvent>> eventHandlers;

	static {
		markerStage = new Stage();
		markerStage.initStyle(StageStyle.UNDECORATED);
		var dummy = new Rectangle(3, 10, Color.web("#555555"));
		var markerStack = new StackPane();
		markerStack.getChildren().add(dummy);
		markerStage.setScene(new Scene(markerStack));
	}

	/**
	 * Create a new draggable tab. This can be added to any normal TabPane,
	 * however a TabPane with draggable tabs must *only* have DraggableTabs,
	 * normal tabs and DrragableTabs mixed will cause issues!
	 * 
	 * @param text   the text to appear on the tag label.
	 * @param editor the editor to use for handling events.
	 */
	public DraggableTabController(String text, IEventHandler editor) {
		eventHandlers = new ArrayList<>();
		nameLabel = new Label(text);
		setGraphic(nameLabel);
		detachable = true;
		dragStage = new Stage();
		dragStage.initStyle(StageStyle.UNDECORATED);
		var dragStagePane = new StackPane();
		dragStagePane.setStyle("-fx-background-color:#DDDDDD;");
		dragText = new Text(text);
		StackPane.setAlignment(dragText, Pos.CENTER);
		dragStagePane.getChildren().add(dragText);
		dragStage.setScene(new Scene(dragStagePane));
		nameLabel.setOnMouseDragged(t -> {
			dragStage.setWidth(nameLabel.getWidth() + 10);
			dragStage.setHeight(nameLabel.getHeight() + 10);
			dragStage.setX(t.getScreenX());
			dragStage.setY(t.getScreenY());
			dragStage.show();
			var screenPoint = new Point2D(t.getScreenX(), t.getScreenY());
			tabPanes.add(getTabPane());
			var data = getInsertData(screenPoint);
			if (data == null || data.insertPane().getTabs().isEmpty())
				markerStage.hide();
			else {
				var index = data.index();
				var end = false;
				if (index == data.insertPane().getTabs().size()) {
					end = true;
					index--;
				}
				var rect = getAbsoluteRect(data.insertPane().getTabs().get(index));
				if (end)
					markerStage.setX(rect.getMaxX() + 13);
				else
					markerStage.setX(rect.getMinX());
				markerStage.setY(rect.getMaxY() + 10);
				markerStage.show();
			}
		});
		nameLabel.setOnMouseReleased(t -> {
			markerStage.hide();
			dragStage.hide();
			if (!t.isStillSincePress()) {
				var screenPoint = new Point2D(t.getScreenX(), t.getScreenY());
				var oldTabPane = getTabPane();
				var oldIndex = oldTabPane.getTabs().indexOf(DraggableTabController.this);
				tabPanes.add(oldTabPane);
				var insertData = getInsertData(screenPoint);
				if (insertData != null) {
					var addIndex = insertData.index();
					if (oldTabPane == insertData.insertPane() && oldTabPane.getTabs().size() == 1)
						return;
					oldTabPane.getTabs().remove(DraggableTabController.this);
					if (oldIndex < addIndex && oldTabPane == insertData.insertPane())
						addIndex--;
					if (addIndex > insertData.insertPane().getTabs().size())
						addIndex = insertData.insertPane().getTabs().size();
					insertData.insertPane().getTabs().add(addIndex, DraggableTabController.this);
					insertData.insertPane().selectionModelProperty().get().select(addIndex);
					return;
				}
				if (!detachable)
					return;
				newStage = new Stage();
				var topPane = new BorderPane();
				var pane = new TabPane();
				pane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
				tabPanes.add(pane);
				newStage.setOnHiding(t1 -> tabPanes.remove(pane));
				getTabPane().getTabs().remove(DraggableTabController.this);
				pane.getTabs().add(DraggableTabController.this);
				pane.getTabs().addListener((ListChangeListener<Tab>) change -> {
					if (pane.getTabs().isEmpty())
						newStage.hide();
				});
				topPane.setCenter(pane);
				topPane.setTop(DI.get(MenuBar.class));
				var spawnScene = new Scene(topPane);
				spawnScene.getStylesheets()
						.add(getClass().getResource("/css/styles.css").toExternalForm());
				spawnScene.addEventHandler(KeyEvent.ANY, e -> editor.onKeyEvent(e));
				spawnScene.addEventHandler(MouseEvent.ANY, e -> editor.onMouseEvent(e));
				newStage.setOnCloseRequest(e -> {
					eventHandlers.forEach(h -> h.handle(e));
				});
				newStage.setScene(spawnScene);
				newStage.initStyle(StageStyle.UTILITY);
				newStage.setX(t.getScreenX());
				newStage.setY(t.getScreenY());
				newStage.show();
				pane.requestLayout();
				pane.requestFocus();
			}
		});
	}

	/**
	 * Add a listener that will be invoked when a tab is closed.
	 * 
	 * @param e the event handler to add.
	 */
	public void addOnClosedListener(EventHandler<WindowEvent> e) {
		eventHandlers.add(e);
	}

	/**
	 * Set whether it's possible to detach the tab from its pane and move it to
	 * another pane or another window. Defaults to true.
	 * 
	 * @param detachable true if the tab should be detachable, false otherwise.
	 */
	public void setDetachable(boolean detachable) {
		this.detachable = detachable;
	}

	/**
	 * Set the label text on this draggable tab. This must be used instead of
	 * setText() to set the label, otherwise weird side effects will result!
	 * 
	 * @param text the label text for this tab.
	 */
	public void setLabelText(String text) {
		nameLabel.setText(text);
		dragText.setText(text);
	}

	/**
	 * Set the label text to be highlighted.
	 * Useful for indicating "unsaved changes"
	 */
	public void setHighlight() {
		nameLabel.getStyleClass().add(Styles.WARNING);
	}

	/**
	 * Set the label text to not be highlighted.
	 * Useful for indicating "unsaved changes"
	 */
	public void unsetHighlight() {
		nameLabel.getStyleClass().removeAll(Styles.WARNING);
	}

	private InsertData getInsertData(Point2D screenPoint) {
		for (var tabPane : tabPanes) {
			var tabAbsolute = getAbsoluteRect(tabPane);
			if (tabAbsolute.contains(screenPoint)) {
				var tabInsertIndex = 0;
				if (!tabPane.getTabs().isEmpty()) {
					var firstTabRect = getAbsoluteRect(tabPane.getTabs().get(0));
					if (firstTabRect.getMaxY() + 60 < screenPoint.getY()
							|| firstTabRect.getMinY() > screenPoint.getY()) {
						return null;
					}
					var lastTabRect = getAbsoluteRect(
							tabPane.getTabs().get(tabPane.getTabs().size() - 1));
					if (screenPoint.getX() < (firstTabRect.getMinX()
							+ firstTabRect.getWidth() / 2)) {
						tabInsertIndex = 0;
					} else if (screenPoint.getX() > (lastTabRect.getMaxX()
							- lastTabRect.getWidth() / 2)) {
						tabInsertIndex = tabPane.getTabs().size();
					} else {
						for (var i = 0; i < tabPane.getTabs().size() - 1; i++) {
							var leftTab = tabPane.getTabs().get(i);
							var rightTab = tabPane.getTabs().get(i + 1);
							if (leftTab instanceof DraggableTabController
									&& rightTab instanceof DraggableTabController) {
								var leftTabRect = getAbsoluteRect(leftTab);
								var rightTabRect = getAbsoluteRect(rightTab);
								if (betweenX(leftTabRect, rightTabRect,
										screenPoint.getX())) {
									tabInsertIndex = i + 1;
									break;
								}
							}
						}
					}
				}
				return new InsertData(tabInsertIndex, tabPane);
			}
		}
		return null;
	}

	private Rectangle2D getAbsoluteRect(Control node) {
		return new Rectangle2D(
				node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY())
						.getX() + node.getScene().getWindow().getX(),
				node.localToScene(node.getLayoutBounds().getMinX(), node.getLayoutBounds().getMinY())
						.getY() + node.getScene().getWindow().getY(),
				node.getWidth(),
				node.getHeight());
	}

	private Rectangle2D getAbsoluteRect(Tab tab) {
		var node = ((DraggableTabController) tab).getLabel();
		return getAbsoluteRect(node);
	}

	private Label getLabel() {
		return nameLabel;
	}

	private boolean betweenX(Rectangle2D r1, Rectangle2D r2, double xPoint) {
		var lowerBound = r1.getMinX() + r1.getWidth() / 2;
		var upperBound = r2.getMaxX() - r2.getWidth() / 2;
		return xPoint >= lowerBound && xPoint <= upperBound;
	}

	@Override
	public void addEditor(ModelEditorController editor) {
		setContent(editor);
	}
}
