package dk.gtz.graphedit.demo.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class SearchController {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private VBox dataContainer;
    @FXML
    private TableView tableView;
    
    @FXML
    private void initialize() {
        // search panel
        searchButton.setText("Search");
        searchButton.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");

        initTable();
    }

    private void initTable() {        
        tableView = new TableView<>();
        var id = new TableColumn("ID");
        var name = new TableColumn("NAME");
        var employed = new TableColumn("EMPLOYED");
        tableView.getColumns().addAll(id, name, employed);
        dataContainer.getChildren().add(tableView);
    }
}
