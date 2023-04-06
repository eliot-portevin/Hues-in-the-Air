package server;

import gui.Colours;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;

import java.awt.*;

public class Cube {
  private Vector2D position;
  private Vector2D velocity;
  private Vector2D size;
  private double jumpHeight;
  private boolean canJump;
  private Pane gameRoot;
  public Rectangle rectangle = new Rectangle();

  public Cube(Pane gameRoot, Vector2D position, Vector2D velocity, Vector2D size) {
    this.position = position;
    this.velocity = velocity;
    this.size = size;
    this.gameRoot = gameRoot;
    spawnCube();
  }

  public void spawnCube() {
    rectangle.setX(position.getX());
    rectangle.setY(position.getY());
    rectangle.setWidth(size.getX());
    rectangle.setHeight(size.getX());
    gameRoot.getChildren().add(rectangle);
  }
  public void jump() {

  }

  public void resetPosition() {

  }
}
