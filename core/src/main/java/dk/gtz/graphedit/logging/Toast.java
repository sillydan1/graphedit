package dk.gtz.graphedit.logging;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import dk.gtz.graphedit.viewmodel.ViewModelEditorSettings;
import dk.yalibs.yadi.DI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Graphedit toast utility. Use this to create notification "toasts" / popups for the user.
 */
public final class Toast {
    private static final Logger logger = LoggerFactory.getLogger(Toast.class);
    private static StackPane toaster;
    private Toast() {}

    /**
     * Set the {@link StackPane} of which to place toast notification popups
     * @param toasterPane the parent pane to show all future popups
     */
    public static void initialize(StackPane toasterPane) {
	Toast.toaster = toasterPane;
    }

    /**
     * Show an "info" styled toast.
     * @param msg The message to display
     */
    public static void info(String msg) {
	if(DI.get(ViewModelEditorSettings.class).showInfoToasts().get())
	    notify(msg, new FontIcon(BootstrapIcons.INFO_CIRCLE), Duration.seconds(3), Styles.ACCENT);
    }

    /**
     * Show an "success" styled toast.
     * @param msg The message to display
     */
    public static void success(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.CHECK_CIRCLE), Duration.seconds(1), Styles.SUCCESS);
    }

    /**
     * Show an "warning" styled toast.
     * @param msg The message to display
     */
    public static void warn(String msg) {
	if(DI.get(ViewModelEditorSettings.class).showWarnToasts().get())
	notify(msg, new FontIcon(BootstrapIcons.EXCLAMATION_TRIANGLE), Duration.seconds(7), Styles.WARNING);
    }

    /**
     * Show an "error" styled toast.
     * @param msg The message to display
     */
    public static void error(String msg) {
	if(DI.get(ViewModelEditorSettings.class).showErrorToasts().get())
	    notify(msg, new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE), Duration.INDEFINITE, Styles.DANGER);
    }

    /**
     * Show a "trace" styled toast.
     * @param msg The message to display
     */
    public static void trace(String msg) {
	if(DI.get(ViewModelEditorSettings.class).showTraceToasts().get())
	    notify(msg, new FontIcon(BootstrapIcons.ARCHIVE), Duration.seconds(1), "");
    }

    private static void notify(String msg, FontIcon icon, Duration showDuration, String... styles) {
	var notification = new Notification(msg, icon);
	notification.getStyleClass().add(Styles.ELEVATED_1);
	notification.getStyleClass().addAll(styles);
	notification.setPrefWidth(Region.USE_COMPUTED_SIZE);
	notification.setPrefHeight(Region.USE_PREF_SIZE);
	notification.setMaxHeight(Region.USE_PREF_SIZE);
	StackPane.setAlignment(notification, Pos.TOP_RIGHT);
	StackPane.setMargin(notification, new Insets(10, 10, 0, 0));
	notification.setOnClose(e -> {
	    if(!toaster.getChildren().contains(notification))
		return;
	    var out = Animations.fadeOutUp(notification, Duration.millis(250));
	    out.setOnFinished(f -> toaster.getChildren().remove(notification));
	    out.playFromStart();
	});
	var timeline = new Timeline(new KeyFrame(showDuration,
		    event -> notification.getOnClose().handle(event)));
        timeline.play();
	notification.setOnMouseEntered(e -> timeline.pause());
	notification.setOnMouseExited(e -> timeline.playFromStart());
	toaster.getChildren().add(notification);
    	Animations.fadeIn(notification, Duration.millis(250)).playFromStart();
    }
}

