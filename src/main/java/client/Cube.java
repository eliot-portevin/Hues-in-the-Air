package client;

import gui.Colours;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cube {
  protected Vector2D position;
  protected Vector2D velocity = new Vector2D(0, 0);
  public Vector2D size;
  private double jumpHeight;
  private boolean canJump = true;
  private Pane gameRoot;
  public ArrayList<Node> platforms;
  public ArrayList<Node> death_platforms;
  public int gridSize;
  protected Rectangle rectangle = new Rectangle();
  private final double gravitiy_scalar = 0.4;
  private Vector2D g = new Vector2D(0, gravitiy_scalar);


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

  public void check_for_white_block_hit() {
    for (Node platform : death_platforms) {
      if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())){
        resetPosition();
      }
    }
  }

  public void setPositionTo(double x, double y) { // Sets the position of the cube to the given x and y
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }

  public void moveX(double value) {
    boolean movingRight = value > 0;

    for (int i = 0; i < Math.abs(value); i++) {
      for (Node platform : platforms) {
        if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
          if (movingRight) {
            if (rectangle.getTranslateX() + size.getX() == platform.getTranslateX()) {
              if(rectangle.getTranslateY() + size.getY() != platform.getTranslateY() && rectangle.getTranslateY() != platform.getTranslateY() + gridSize) {
                canJump = true;
                g.setX(gravitiy_scalar);
                g.setY(0);
                System.out.println("X: " + g.getX() + " Y: " + g.getY());
                return;
              }

            }
          } else {
            if (rectangle.getTranslateX() == platform.getTranslateX() + gridSize) {
              if(rectangle.getTranslateY() + size.getY() != platform.getTranslateY() && rectangle.getTranslateY() != platform.getTranslateY() + gridSize) {
                canJump = true;
                g.setX(-gravitiy_scalar);
                g.setY(0);
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
                canJump = true;
                g.setX(0);
                g.setY(gravitiy_scalar);
                return;
              }
            }
          } else {
            if (rectangle.getTranslateY() == platform.getTranslateY() + gridSize) {
              if(rectangle.getTranslateX() + size.getX() != platform.getTranslateX() && rectangle.getTranslateX() != platform.getTranslateX() + gridSize) {
                canJump = true;
                g.setX(0);
                g.setY(-gravitiy_scalar);
                return;
              }
            }
          }
        }
      }
      move1Y(movingDown);
    }
  }

  public void move(Vector2D velocity) {
    this.velocity.setY(this.velocity.getY()+this.g.getY());
    this.velocity.setX(this.velocity.getX()+this.g.getX());
    this.moveX(velocity.getX());
    this.moveY(velocity.getY());
  }

  public void jump() {
    if (canJump) {
      velocity.setY(-40*g.getY());
      velocity.setX(-40*g.getX());
      canJump = false;
    }
    //Vector2D startPosition = new Vector2D(position.getX(), position.getY());

    /*AnimationTimer jumpTimer = new AnimationTimer() {
      @Override
      public void handle(long now) {
        changePosition();
        velocity.setY(velocity.getY() + g.getY());
        if(position.equals(startPosition)) {
          this.stop();
        }
      }
    };
    jumpTimer.start();*/
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
    rectangle.setFill(Colours.DARK_GREY.getHex());
  }
}
