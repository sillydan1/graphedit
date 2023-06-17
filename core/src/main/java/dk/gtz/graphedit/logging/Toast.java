package dk.gtz.graphedit.logging;

import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public final class Toast {
    private static StackPane toaster;

    public static void initialize(StackPane toasterPane) {
	Toast.toaster = toasterPane;
    }

    public static void info(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.INFO_CIRCLE), Styles.ACCENT);
    }

    public static void success(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.CHECK_CIRCLE), Styles.SUCCESS);
    }

    public static void warn(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.EXCLAMATION_TRIANGLE), Styles.WARNING);
    }

    public static void error(String msg) {
	notify(msg, new FontIcon(BootstrapIcons.EXCLAMATION_CIRCLE), Styles.DANGER);
    }

    private static void notify(String msg, FontIcon icon, String... styles) {
	var notification = new Notification(msg, icon);
	notification.getStyleClass().add(Styles.ELEVATED_1);
	notification.getStyleClass().addAll(styles);
	notification.setOnClose(e -> Animations.flash(notification).playFromStart());
	notification.setPrefHeight(Region.USE_PREF_SIZE);
	notification.setMaxHeight(Region.USE_PREF_SIZE);
	StackPane.setAlignment(notification, Pos.TOP_RIGHT);
	StackPane.setMargin(notification, new Insets(10, 10, 0, 0));
	notification.setOnClose(e -> {
	    var out = Animations.slideOutUp(notification, Duration.millis(250));
	    out.setOnFinished(f -> toaster.getChildren().remove(notification));
	    out.playFromStart();
	});
	var in = Animations.slideInDown(notification, Duration.millis(250));
	toaster.getChildren().add(notification);
	in.playFromStart();
    }
}

