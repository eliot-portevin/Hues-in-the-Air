package server;

import gui.Colours;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class Cube {
  private Vector2D position;
  private Vector2D velocity;
  public Vector2D size;
  private double jumpHeight;
  private boolean canJump;
  private Pane gameRoot;
  protected Rectangle rectangle = new Rectangle();

  public Cube(Pane gameRoot, Vector2D position, Vector2D size) {
    this.position = position;
    this.size = size;
    this.gameRoot = gameRoot;
    spawnCube();
  }

  public void spawnCube() { // Spawns the cube at the given position with given size and adds it to the gameRoot
    rectangle = new Rectangle(size.getX(), size.getY());
    this.setPositionTo(position.getX(), position.getY());
    rectangle.setFill(Colours.GREEN.getHex());
    gameRoot.getChildren().add(rectangle);
  }

  public void setPositionTo(double x, double y) { // Sets the position of the cube to the given x and y
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }

  public void move1X(boolean movingRight) { // Moves the cube 1 pixel on the x axis
    this.rectangle.setTranslateX(this.rectangle.getTranslateX() + (movingRight ? 1 : -1));
  }

  public void move1Y(boolean movingDown) { // Moves the cube 1 pixel on the y axis
    this.rectangle.setTranslateY(this.rectangle.getTranslateY() + (movingDown ? 1 : -1));
  }

  public void jump() {

  }

  public void resetPosition() {

  }
}
