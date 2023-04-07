package client;

import gui.Colours;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cube {
  protected Vector2D position;
  protected Vector2D velocity = new Vector2D(0.01, 0);
  public Vector2D size;
  private double jumpHeight;
  private boolean canJump;
  private Pane gameRoot;
  public ArrayList<Node> platforms;
  public int gridSize;
  protected Rectangle rectangle = new Rectangle();
  private final Vector2D g = new Vector2D(0, 0.01);


  public Cube(Pane gameRoot, Vector2D position, Vector2D size) {
    this.position = position;
    this.velocity = velocity;
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

  public void changePosition() {
    //Todo: Add gravity
    //Todo: Make jump good xD
    moveX(1);
    moveY(velocity.getY());
  }

  public void moveX(double value) {
    boolean movingRight = value > 0;

    for (int i = 0; i < Math.abs(value); i++) {
      for (Node platform : platforms) {
        if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
          if (movingRight) {
            if (rectangle.getTranslateX() + size.getX() == platform.getTranslateX()) {
              if(rectangle.getTranslateY() + size.getY() != platform.getTranslateY() && rectangle.getTranslateY() != platform.getTranslateY() + gridSize) {
                return;
              }

            }
          } else {
            if (rectangle.getTranslateX() == platform.getTranslateX() + gridSize) {
              if(rectangle.getTranslateY() + size.getY() != platform.getTranslateY() && rectangle.getTranslateY() != platform.getTranslateY() + gridSize) {
                return;
              }
            }
          }
        }
      }
      move1X(movingRight);
    }
  }

  public void moveY(double value) {
    boolean movingDown = value > 0;

    for (int i = 0; i < Math.abs(value); i++) {
      for (Node platform : platforms) {
        if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
          if (movingDown) {
            if (rectangle.getTranslateY() + size.getY() == platform.getTranslateY()) {
              if(rectangle.getTranslateX() + size.getX() != platform.getTranslateX() && rectangle.getTranslateX() != platform.getTranslateX() + gridSize) {
                return;
              }
            }
          } else {
            if (rectangle.getTranslateY() == platform.getTranslateY() + gridSize) {
              if(rectangle.getTranslateX() + size.getX() != platform.getTranslateX() && rectangle.getTranslateX() != platform.getTranslateX() + gridSize) {
                return;
              }
            }
          }
        }
      }
      move1Y(movingDown);
    }
  }

  public void jump() {
    velocity.setY(-1);
    Vector2D startPosition = new Vector2D(position.getX(), position.getY());

    AnimationTimer jumpTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        changePosition();
        velocity.setY(velocity.getY() + g.getY());
        if(position.equals(startPosition)) {
          this.stop();
        }
      }
    };
    jumpTimer.start();
  }

  public void move1X(boolean movingRight) { // Moves the cube 1 pixel on the x axis
    this.rectangle.setTranslateX(this.rectangle.getTranslateX() + (movingRight ? 1 : -1));
    this.position.setX(this.rectangle.getTranslateX() + (movingRight ? 1 : -1));
  }

  public void move1Y(boolean movingDown) { // Moves the cube 1 pixel on the y axis
    this.rectangle.setTranslateY(this.rectangle.getTranslateY() + (movingDown ? 1 : -1));
    this.position.setY(this.rectangle.getTranslateY() + (movingDown ? 1 : -1));
  }

  public void resetPosition() {

  }
}
