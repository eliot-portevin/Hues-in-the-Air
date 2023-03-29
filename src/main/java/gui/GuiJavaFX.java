package gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;import javafx.stage.Stage;

public class GuiJavaFX extends Application {

  private int WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
  private int HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();

  @Override
  public void start(Stage stage) throws Exception {
    stage.setTitle("Hues in the Air");
    stage.setFullScreen(true);
    stage.setResizable(false);

    Group root = new Group();
    Scene scene = new Scene(root, Colours.BLACK.getHex());

    Text title = new Text();
    title.setText("Hues in the Air");
    title.setFont(Font.font("Verdana", 50));
    title.setFill(Colours.WHITE.getHex());
    title.setX(this.WIDTH / 2 - title.getLayoutBounds().getWidth() / 2);
    title.setY(this.HEIGHT / 3);

    root.getChildren().add(title);

    stage.setScene(scene);
    stage.show();
  }
}
