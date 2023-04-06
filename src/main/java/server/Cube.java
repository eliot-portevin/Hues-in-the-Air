package server;

import gui.Colours;
import javafx.scene.shape.Rectangle;

public class Cube {
  private Vector2D position;
  private Vector2D velocity;
  private Vector2D size;
  private double jumpHeight;
  private boolean canJump;
  protected Rectangle rectangle = new Rectangle();

  public Cube(Vector2D position, Vector2D velocity, Vector2D size) {
    this.position = position;
    this.velocity = velocity;
    this.size = size;
    spawnCube();
  }

  public void spawnCube() {
    rectangle = new Rectangle(50,50);
    this.setPositionTo(100, 100);
    rectangle.setFill(Colours.GREEN.getHex());
  }

  public void setPositionTo(double x, double y) {
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }

  public void jump() {

  }

  public void resetPosition() {

  }
}
