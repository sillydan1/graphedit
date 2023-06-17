package dk.gtz.graphedit.logging;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public final class Toast {
    private static final Logger logger = LoggerFactory.getLogger(Toast.class);
    private static StackPane toaster;

    public static void initialize(StackPane toasterPane) {
	Toast.toaster = toasterPane;
    }

    public static void info(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.INFO_CIRCLE), Duration.seconds(5), Styles.ACCENT);
    }

    public static void success(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.CHECK_CIRCLE), Duration.seconds(3), Styles.SUCCESS);
    }

    public static void warn(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.EXCLAMATION_TRIANGLE), Duration.seconds(10), Styles.WARNING);
    }

    public static void error(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE), Duration.INDEFINITE, Styles.DANGER);
    }

    private static void notify(String msg, FontIcon icon, Duration showDuration, String... styles) {
	// NOTE: It is important to not use logger.info/warn/error here - since you'd get an infinite loop then
	logger.trace(msg);
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
    	Animations.fadeInDown(notification, Duration.millis(250)).playFromStart();
    }
}

