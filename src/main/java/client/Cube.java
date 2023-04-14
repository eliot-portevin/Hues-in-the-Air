package client;

import gui.Colours;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cube {
  protected Vector2D position;
  public Vector2D size;
  private final double jumpHeight = 30;
  private boolean canJump = true;
  private Pane gameRoot;
  public ArrayList<Node> platforms;
  public ArrayList<Node> death_platforms;
  public int gridSize;
  protected Rectangle rectangle = new Rectangle();
  private final double gravity_scalar = 0.3;
  private final double speed = 3;
  private Vector2D g = new Vector2D(0, gravity_scalar);
  protected Vector2D velocity = new Vector2D(speed, 0);
  private double y0 = 100000;
  private boolean y0passed;

  private Vector2D moveBuffer;


  public Cube(Pane gameRoot, Vector2D position, Vector2D size) {
    this.position = position;
    this.size = size;
    this.gameRoot = gameRoot;
    spawnCube();
  }

  /**
   * Spawns the cube at the given position with given size and adds it to the gameRoot
   */
  public void spawnCube() {
    rectangle = new Rectangle(size.getX(), size.getY());
    this.setPositionTo(position.getX(), position.getY());
    rectangle.setFill(Colours.GREEN.getHex());
    gameRoot.getChildren().add(rectangle);
  }

  /**
   * Checks for collisions with the white blocks and calls resetsPosition if it collides with one
   */
  public void checkForWhiteBlockHit() {
    for (Node platform : death_platforms) {
      if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())){
        resetPosition();
      }
    }
  }

  /**
   * Sets position of the cube to the given x and y
   */
  public void setPositionTo(double x, double y) {
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }
  /**
   * Sets Gravity and velocity correctly when gravitation pulls the cube to the right
   */
  public void setGravityAndVelocityRight() {
    canJump = true;
    velocity.setX(0);
    if (g.getX() == gravity_scalar) {
      velocity.setX(0);
      if (velocity.getY() > 0) {
        velocity.setY(speed);
      } else {
        velocity.setY(-speed);
      }
    } else if (g.getX() == -gravity_scalar) {
      g.setX(gravity_scalar);
    } else if (g.getY() == gravity_scalar) {
      g.setY(0);
      g.setX(gravity_scalar);
      velocity.setY(-speed);
    } else if (g.getY() == -gravity_scalar) {
      g.setY(0);
      g.setX(gravity_scalar);
      velocity.setY(speed);
    }
  }
  /**
   * Sets Gravity and velocity correctly when gravitation pulls the cube to the left
   */
  public void setGravityAndVelocityLeft() {
    canJump = true;
    velocity.setX(0);
    if (g.getX() == -gravity_scalar) {
      if (velocity.getY() > 0) {
        velocity.setY(speed);
      } else {
        velocity.setY(-speed);
      }
    } else if (g.getX() == gravity_scalar) {
      g.setX(-gravity_scalar);
    } else if (g.getY() == gravity_scalar) {
      g.setY(0);
      g.setX(-gravity_scalar);
      velocity.setY(-speed);
    } else if (g.getY() == -gravity_scalar) {
      g.setY(0);
      g.setX(-gravity_scalar);
      velocity.setY(speed);
    }
  }
  /**
   * Sets Gravity and velocity correctly when gravitation pulls the cube up
   */
  public void setGravityAndVelocityUp() {
    canJump = true;
    velocity.setY(0);
    if (g.getY() == -gravity_scalar) {
      if (velocity.getX() > 0) {
        velocity.setX(speed);
      } else {
        velocity.setX(-speed);
      }
    } else if (g.getY() == gravity_scalar) {
      g.setY(-gravity_scalar);
    } else if (g.getX() == gravity_scalar) {
      g.setX(0);
      g.setY(-gravity_scalar);
      velocity.setX(-speed);
    } else if (g.getX() == -gravity_scalar) {
      g.setX(0);
      g.setY(-gravity_scalar);
      velocity.setX(speed);
    }
  }
  /**
   * Sets Gravity and velocity correctly when gravitation pulls the cube down
   */
  public void setGravityAndVelocityDown() {
    canJump = true;
    velocity.setY(0);
    if (g.getY() == gravity_scalar) {
      if (velocity.getX() > 0) {
        velocity.setX(speed);
      } else {
        velocity.setX(-speed);
      }
    } else if (g.getY() == -gravity_scalar) {
      g.setY(gravity_scalar);
    } else if (g.getX() == gravity_scalar) {
      g.setX(0);
      g.setY(gravity_scalar);
      velocity.setX(-speed);
    } else if (g.getX() == -gravity_scalar) {
      g.setX(0);
      g.setY(gravity_scalar);
      velocity.setX(speed);
    }
  }
  /**
   * Rotates Gravitation if cube goes out of bounds if cube is moving in x direction
   */
  public void gravityRotationX(){
    if (g.getY() > 0 && !y0passed) {
      if (y0 < position.getY()) {
        y0passed = true;
        g.setY(-gravity_scalar);
        velocity.setX(-velocity.getX());
        velocity.setY(-jumpHeight*g.getY());
      }
    } else if (g.getY() < 0 && !y0passed) {
      if (y0 > position.getY()) {
        y0passed = true;
        g.setY(gravity_scalar);
        velocity.setX(-velocity.getX());
        velocity.setY(-jumpHeight*g.getY());
      }
    }
  }

  /**
   * Rotates Gravitation if cube goes out of bounds if cube is moving in y direction
   */
  public void gravityRotationY(){
    if (g.getX() > 0 && !y0passed) {
      if (y0 < position.getX()) {
        y0passed = true;
        g.setX(-gravity_scalar);
        velocity.setY(-velocity.getY());
        velocity.setX(-jumpHeight*g.getX());
      }
    } else if (g.getX() < 0 && !y0passed) {
      if (y0 > position.getX()) {
        y0passed = true;
        g.setX(gravity_scalar);
        velocity.setY(-velocity.getY());
        velocity.setX(-jumpHeight*g.getX());
      }
    }
  }
  /**
   * Checks if the cube is colliding with anything, if not it moves the cube by 1 pixel steps in x direction and checks again
   * If the cube is colliding with something it stops the cube from clipping into the object and sets the velocity in that direction to 0 and sets canJump to true
   * On collision the cubes gravity is also set to the opposite of the normal of the surface it is colliding with
   */
  public void moveX(double value) {
    boolean movingRight = value > 0;
    if (!canJump) {
      //if(moveBuffer.getX() > 1) {
        gravityRotationX();
        for (int i = 0; i < Math.abs(value); i++) {
          for (Node platform : platforms) {
            if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
              if (movingRight) {
                if (rectangle.getTranslateX() + size.getX() == platform.getTranslateX()) { // Checks if the cube is colliding with a platform on the right side
                  if (rectangle.getTranslateY() + size.getY() != platform.getTranslateY() && rectangle.getTranslateY() != platform.getTranslateY() + gridSize) { // For edge cases where the cube should slide over a corner but instead gets stuck
                    setGravityAndVelocityRight();
                    return;
                  }
                }
              } else {
                if (rectangle.getTranslateX() == platform.getTranslateX() + gridSize) { // Checks if the cube is colliding with a platform on the left side
                  if (rectangle.getTranslateY() + size.getY() != platform.getTranslateY() && rectangle.getTranslateY() != platform.getTranslateY() + gridSize) {
                      setGravityAndVelocityLeft();
                      return;
                  }
                }
              }
            }
          }
          move1X(movingRight);
        }
      //}
    } else {
      for (int i = 0; i < Math.abs(value); i++) {
        move1X(movingRight);
      }
    }
  }

  /**
   * Checks if the cube is colliding with anything, if not it moves the cube by 1 pixel steps in y direction and checks again
   * If the cube is colliding with something it stops the cube from clipping into the object and sets the velocity in that direction to 0 and sets canJump to true
   * On collision the cubes gravity is also set to the opposite of the normal of the surface it is colliding with
   */
  public void moveY(double value) {
    boolean movingDown = value > 0;
    if (!canJump) {
        gravityRotationY();
      for (int i = 0; i < Math.abs(value); i++) {
        for (Node platform : platforms) {
          if (rectangle.getBoundsInParent().intersects(platform.getBoundsInParent())) {
            if (movingDown) {
              if (rectangle.getTranslateY() + size.getY() == platform.getTranslateY()) { // Checks if the cube is colliding with a platform on the bottom side
                if (rectangle.getTranslateX() + size.getX() != platform.getTranslateX() && rectangle.getTranslateX() != platform.getTranslateX() + gridSize) { // For edge cases where the cube should slide over a corner but instead gets stuck
                    setGravityAndVelocityDown();
                    return;
                }
              }
            } else {
              if (rectangle.getTranslateY() == platform.getTranslateY() + gridSize) { // Checks if the cube is colliding with a platform on the top side
                if (rectangle.getTranslateX() + size.getX() != platform.getTranslateX() && rectangle.getTranslateX() != platform.getTranslateX() + gridSize) { // For edge cases where the cube should slide over a corner but instead gets stuck
                    setGravityAndVelocityUp();
                    return;
                }
              }
            }
          }
        }
        move1Y(movingDown);
      }
    } else {
      for (int i = 0; i < Math.abs(value); i++) {
        move1Y(movingDown);
      }
    }
  }

  /**
   * updates the velocity of the cube and moves it by the given velocity
   */
  public void move(Vector2D velocity) {
    if (!canJump) {
      velocity.setY(velocity.getY()+g.getY());
      velocity.setX(velocity.getX()+g.getX());
    }
    moveX(velocity.getX());
    moveY(velocity.getY());
  }

  /**
   * Makes the cube jump by setting the velocity of the cube to the opposite of the gravity vector
   */
  public void jump() {
    if (canJump) {
      if(g.getX() > gravity_scalar/2) {
        y0 = position.getX() + gridSize + size.getX();
        y0passed = false;
      } else if(g.getX() < -gravity_scalar/2) {
        y0 = position.getX() - gridSize;
        y0passed = false;
      } else if(g.getY() > gravity_scalar/2) {
        y0 = position.getY() + gridSize + size.getY();
        y0passed = false;
      } else if(g.getY() < -gravity_scalar/2) {
        y0 = position.getY() - gridSize;
        y0passed = false;
      }
      if (velocity.getY() < 1 && velocity.getY() > -1) {
        velocity.setY(-jumpHeight*g.getY());
      } else {
        velocity.setX(-jumpHeight*g.getX());
      }
      canJump = false;
    }
  }

  /**
   * Moves the cube 1 pixel on the x-axis
   */
  public void move1X(boolean movingRight) {
    this.rectangle.setTranslateX(this.rectangle.getTranslateX() + (movingRight ? 1 : -1));
    this.position.setX(this.rectangle.getTranslateX() + (movingRight ? 1 : -1));
  }

  /**
   * Moves the cube 1 pixel on the y axis
   */
  public void move1Y(boolean movingDown) {
    this.rectangle.setTranslateY(this.rectangle.getTranslateY() + (movingDown ? 1 : -1));
    this.position.setY(this.rectangle.getTranslateY() + (movingDown ? 1 : -1));
  }

  /**
   * Called upon collision with a white block, is responsible for the death effects of the cube.
   */
  public void resetPosition() {
    rectangle.setFill(Colours.DARK_GREY.getHex());
  }
}
