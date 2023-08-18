package dk.gtz.graphedit.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import dk.gtz.graphedit.viewmodel.IBufferContainer;
import dk.gtz.graphedit.viewmodel.IFocusable;
import dk.yalibs.yadi.DI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class SearchPaneController implements IFocusable {
    private static Logger logger = LoggerFactory.getLogger(SearchResultController.class);
    @FXML
    private Button searchButton; // TODO: Consider removing the search button
    @FXML
    private TextField searchField;
    @FXML
    private Button closeButton;
    @FXML
    private VBox root;
    private ObservableList<SearchResultController> results;
    private ListView<SearchResultController> resultsView;
    private Optional<Runnable> onCloseRunner = Optional.empty();
    private List<Runnable> focusEventHandlers;
    private IBufferContainer bufferContainer;

    @FXML
    private void initialize() {
	focusEventHandlers = new ArrayList<>();
	bufferContainer = DI.get(IBufferContainer.class);
	initRoot();
	initButtons();
	initResults();
	initSearchEvents();
    }

    private void initRoot() {
	Styles.addStyleClass(root, Styles.BG_DEFAULT);
    }

    private void initButtons() {
	searchButton.setGraphic(new FontIcon(BootstrapIcons.SEARCH));
	closeButton.setOnAction(e -> close());
    }

    private void initResults() {
	results = FXCollections.observableArrayList();
	resultsView = new ListView<>(results);
	Styles.addStyleClass(resultsView, Styles.DENSE);
	Styles.addStyleClass(resultsView, Tweaks.EDGE_TO_EDGE);
	root.getChildren().add(resultsView);
	resultsView.setOnMouseClicked(e -> {
	    if (e.getClickCount() == 2) {
		resultsView.getSelectionModel().getSelectedItem().focus();
		close();
	    }
	});
    }

    private void initSearchEvents() {
	searchField.textProperty().addListener((e,o,n) -> updateSearchResults(n));
	searchField.setOnKeyPressed(this::searchFieldKeyPress);
    }

    private boolean isLastSelected() {
	return resultsView.getSelectionModel().getSelectedIndex() == results.size()-1;
    }

    private boolean isFirstSelected() {
	return resultsView.getSelectionModel().getSelectedIndex() == 0;
    }

    private boolean isNoneSelected() {
	return resultsView.getSelectionModel().getSelectedIndex() == -1;
    }

    private void searchFieldKeyPress(KeyEvent event) {
	if(event.getCode().equals(KeyCode.TAB)) {
	    if(event.isShiftDown()) {
		if(isNoneSelected() || isFirstSelected())
		    resultsView.getSelectionModel().selectLast();
		else
		    resultsView.getSelectionModel().selectPrevious();
	    }
	    else {
		if(isNoneSelected() || isLastSelected())
		    resultsView.getSelectionModel().selectFirst();
		else
		    resultsView.getSelectionModel().selectNext();
	    }
	    event.consume();
	    searchField.requestFocus(); // TODO: This is not right, but it will work for now
	}
	if(event.getCode().equals(KeyCode.ENTER)) {
	    resultsView.getSelectionModel().getSelectedItem().focus();
	    close();
	}
    }

    private void updateSearchResults(String searchTerm) {
	// TODO: Also search the contents of the buffers
	results.clear();
	for(var buffer : bufferContainer.getBuffers().entrySet()) {
	    if(buffer.getKey().contains(searchTerm))
		results.add(new SearchResultController(BootstrapIcons.FILE, buffer.getKey(), buffer.getValue()));
	}
    }

    public void onClose(Runnable onClose) {
	onCloseRunner = Optional.of(onClose);
    }

    public void close() {
	onCloseRunner.ifPresent(Runnable::run);
    }

    @Override
    public void addFocusListener(Runnable focusEventHandler) {
	focusEventHandlers.add(focusEventHandler);
    }

    @Override
    public void focus() {
	searchField.requestFocus();
	focusEventHandlers.forEach(Runnable::run);
    }
}

