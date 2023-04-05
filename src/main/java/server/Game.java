package server;

import gui.Colours;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;

import java.awt.*;

public class Game extends Application {
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Group root = new Group();
    Scene scene = new Scene(root,800,450, Color.BLUE);
    Stage stage = new Stage();

    Rectangle rectangle = new Rectangle();
    rectangle.setX(350);
    rectangle.setY(175);
    rectangle.setWidth(100);
    rectangle.setHeight(100);



    root.getChildren().add(rectangle);
    Button btup = new Button("UP");
    btup.setLayoutX(350);
    btup.setLayoutY(325);

    root.getChildren().add(btup);

    btup.setOnAction(e -> rectangle.setY(rectangle.getY()-10));

    stage.setScene(scene);
    stage.show();


  }

  /*EventHandler<ActionEvent> btupActionEvent = new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
      System.out.println("Pressed up");

      rectangle.setX()

    }
  };*/

}
