package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GuiJavaFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, Color.PALEVIOLETRED);

        stage.setTitle("Opening window");
        stage.setScene(scene);
        stage.show();
    }
}
