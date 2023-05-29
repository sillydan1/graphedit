package dk.gtz.graphedit.logging;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public final class Toast {
    private static Stage primaryStage;

    public static void initialize(Stage primaryStage) {
	Toast.primaryStage = primaryStage;
    }

    public static void show(String msg) {
	var toastMsgTime = 3000;
	var fadeInTime = 300;
	var fadeOutTime= 300;
	show(primaryStage, msg, toastMsgTime, fadeInTime, fadeOutTime);
    }

    public static void show(Stage ownerStage, String toastMsg, int toastDelay, int fadeInDelay, int fadeOutDelay) {
	var toastStage = new Stage();
	toastStage.initOwner(ownerStage);
	toastStage.setResizable(false);
	toastStage.initStyle(StageStyle.TRANSPARENT);

	var text = new Text(toastMsg);
	var root = new StackPane(text);
	root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.2); -fx-padding: 50px;");
	root.setOpacity(0);

	var scene = new Scene(root);
	scene.setFill(Color.TRANSPARENT);
	toastStage.setScene(scene);
	toastStage.show();

	var fadeInTimeline = new Timeline();
	var fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1)); 
	fadeInTimeline.getKeyFrames().add(fadeInKey1);   
	fadeInTimeline.setOnFinished((ae) -> {
	    new Thread(() -> {
		try {
		    Thread.sleep(toastDelay);
		}
		catch (InterruptedException e){
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		var fadeOutTimeline = new Timeline();
		var fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
		fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
		fadeOutTimeline.setOnFinished((aeb) -> toastStage.close()); 
		fadeOutTimeline.play();
	    }).start();
	}); 
	fadeInTimeline.play();
    }
}

