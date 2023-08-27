package dk.gtz.graphedit.viewmodel;

import dk.gtz.graphedit.model.ModelEditorSettings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public record ViewModelEditorSettings(
                DoubleProperty gridSizeX,
                DoubleProperty gridSizeY,
                BooleanProperty gridSnap,
                BooleanProperty useLightTheme,
                StringProperty lastOpenedProject) {

        public ViewModelEditorSettings(ModelEditorSettings settings) {
                this(
                        settings.gridSizeX(),
                        settings.gridSizeY(),
                        settings.gridSnap(),
                        settings.useLightTheme(),
                        settings.lastOpenedProject()
                    );
        }

        public ViewModelEditorSettings(
                        double gridSizeX, 
                        double gridSizeY,
                        boolean gridSnap,
                        boolean useLightTheme,
                        String lastOpenedProject) {
                this(
                        new SimpleDoubleProperty(gridSizeX),
                        new SimpleDoubleProperty(gridSizeY),
                        new SimpleBooleanProperty(gridSnap),
                        new SimpleBooleanProperty(useLightTheme),
                        new SimpleStringProperty(lastOpenedProject)
                    );
        }
}

