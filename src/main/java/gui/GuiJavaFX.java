package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GuiJavaFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, Color.MEDIUMSPRINGGREEN);

        Text title = new Text();
        title.setText("Hues in the Air");
        title.setX(500);
        title.setY(100);
        title.setFont(Font.font("Verdana", 50 ));
        title.setFill(Color.PALEVIOLETRED);

        Line line = new Line();
        line.setStartX(200);
        line.setStartY(200);
        line.setEndX(500);
        line.setEndY(200);

        root.getChildren().add(title);
        root.getChildren().add(line);
        stage.setTitle("Hues in the Air");
        stage.setFullScreen(true);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }
}
