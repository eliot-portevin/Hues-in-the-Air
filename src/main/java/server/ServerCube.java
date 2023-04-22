package server;

import client.Vector2D;
import game.Block;
import gui.Colours;
import java.util.ArrayList;
import java.util.Timer;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class ServerCube {
  // Position, velocity, acceleration
  private final int blocksPerSecond = 8;
  private final double gravity_scalar = 10;

  protected Vector2D position;
  private final double speed = 30;
  protected Vector2D velocity = new Vector2D(0, 0);
  public Vector2D acceleration = new Vector2D(0, 0);
  private int accelerationAngle = 0;

  boolean initialAcceleration = false;

  boolean jumping = true;

  public Vector2D size;
  private final double jumpHeight = 30;
  private boolean canJump = true;
  private final Pane gameRoot;
  public ArrayList<Node> platforms;
  public ArrayList<Node> death_platforms;
  public int gridSize;
  protected Rectangle rectangle = new Rectangle();
  private double y0 = 100000;
  private boolean y0passed;
  public Vector2D start_position = new Vector2D(0, 0);
  private boolean onlyMoveOneDir = false;
  private Vector2D moveBuffer;
  private final Timer timer = new Timer();

  public ServerCube(Pane gameRoot, Vector2D position, Vector2D size) {
    this.position = position;
    this.acceleration.setX(0);
    this.acceleration.setY(size.getX() * blocksPerSecond);
    this.size = size;

    this.gameRoot = gameRoot;

    spawnCube();
  }

  /** Spawns the cube at the given position with given size and adds it to the gameRoot */
  public void spawnCube() {
    rectangle = new Rectangle(size.getX(), size.getY());
    this.setPositionTo(position.getX(), position.getY());
    gameRoot.getChildren().add(rectangle);
  }

  /** Sets position of the cube to the given x and y */
  public void setPositionTo(double x, double y) {
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }

  /** Rotates Gravitation if cube goes out of bounds if cube is moving in x direction */
  public void gravityRotationX() {
    if (acceleration.getY() > 0 && !y0passed) {
      if (y0 < position.getY()) {
        y0passed = true;
        acceleration.setY(-gravity_scalar);
        velocity.setX(-velocity.getX());
        velocity.setY(-jumpHeight * acceleration.getY());
      }
    } else if (acceleration.getY() < 0 && !y0passed) {
      if (y0 > position.getY()) {
        y0passed = true;
        acceleration.setY(gravity_scalar);
        velocity.setX(-velocity.getX());
        velocity.setY(-jumpHeight * acceleration.getY());
      }
    }
  }

  /** Rotates Gravitation if cube goes out of bounds if cube is moving in y direction */
  public void gravityRotationY() {
    if (acceleration.getX() > 0 && !y0passed) {
      if (y0 < position.getX()) {
        y0passed = true;
        acceleration.setX(-gravity_scalar);
        velocity.setY(-velocity.getY());
        velocity.setX(-jumpHeight * acceleration.getX());
      }
    } else if (acceleration.getX() < 0 && !y0passed) {
      if (y0 > position.getX()) {
        y0passed = true;
        acceleration.setX(gravity_scalar);
        velocity.setY(-velocity.getY());
        velocity.setX(-jumpHeight * acceleration.getX());
      }
    }
  }

  /**
   * Makes the cube jump by setting the velocity of the cube to the opposite of the gravity vector
   */
  public void jump() {
    if (!jumping) {
      jumping = true;

      Vector2D jumpVector = new Vector2D(Math.sin(accelerationAngle), Math.cos(accelerationAngle));
      jumpVector.multiplyInPlace(blocksPerSecond);
      jumpVector.multiplyInPlace(size.getX());
      jumpVector.multiplyInPlace(-1);

      velocity.addInPlace(jumpVector);
    }
    /*
    if (canJump) {
      if (acceleration.getX() > gravity_scalar / 2) {
        y0 = position.getX() + gridSize + size.getX();
        y0passed = false;
      } else if (acceleration.getX() < -gravity_scalar / 2) {
        y0 = position.getX() - gridSize;
        y0passed = false;
      } else if (acceleration.getY() > gravity_scalar / 2) {
        y0 = position.getY() + gridSize + size.getY();
        y0passed = false;
      } else if (acceleration.getY() < -gravity_scalar / 2) {
        y0 = position.getY() - gridSize;
        y0passed = false;
      }
      if (velocity.getY() < 1 && velocity.getY() > -1) {
        rectangle.setTranslateY(Math.signum(acceleration.getY()) * -2 + rectangle.getTranslateY());
        velocity.setY(-jumpHeight * acceleration.getY());
      } else {
        rectangle.setTranslateX(Math.signum(acceleration.getX()) * -2 + rectangle.getTranslateX());
        velocity.setX(-jumpHeight * acceleration.getX());
      }
      canJump = false;
      timer.schedule(
          new TimerTask() {
            @Override
            public void run() {
              setOnlyMoveOneDir();
            }
          },
          50);
      onlyMoveOneDir = true;
    }

     */
  }
  /** Moves the cube by the given value in the x direction and updates the position of the cube */
  public void moveValueX(double value) {
    this.rectangle.setTranslateX(this.rectangle.getTranslateX() + value);
    this.position.setX(this.rectangle.getTranslateX());
  }
  /** Moves the cube by the given value in the y direction and updates the position of the cube */
  public void moveValueY(double value) {
    this.rectangle.setTranslateY(this.rectangle.getTranslateY() + value);
    this.position.setY(this.rectangle.getTranslateY());
  }

  /** Called upon collision with a white block, is responsible for the death effects of the cube. */
  public void death() {
    rectangle.setFill(Colours.DARK_GREY.getHex());
  }

  /** only here to prevent the cube from colliding strangely with the walls */
  private void setOnlyMoveOneDir() {
    this.onlyMoveOneDir = false;
  }

  /**
   * Moves the cube by its current velocity and checks for collisions with the given neighbour
   * blocks. If a collision is detected the cube is moved back to the position before the collision
   * and the velocity is set to 0 in the direction of the collision.
   *
   * @param neighbourBlocks the blocks to check for collisions with
   * @param dt the time since the last frame in seconds
   */
  public void move(Block[] neighbourBlocks, double dt) {
    // Update the velocity according to acceleration
    this.velocity.addInPlace(acceleration.multiply(dt));

    // Move cube in x direction and check for collisions
    this.position.setX(this.position.getX() + velocity.getX() * dt);
    this.rectangle.setTranslateX((int) this.position.getX());

    for (Block block : neighbourBlocks) {
      if (block != null) {
        if (this.rectangle
            .getBoundsInParent()
            .intersects(block.getRectangle().getBoundsInParent())) {
          jumping = false;

          // If the block was to the right of the cube before collision
          if (velocity.getX() > 0) {
            this.position.setX(block.getX() - this.rectangle.getWidth() - 1);
            this.setAccelerationAngle(90);
          }
          // If the block was to the left of the cube before collision
          else if (velocity.getX() < 0) {
            this.position.setX(block.getX() + block.getRectangle().getWidth() + 1);
            this.setAccelerationAngle(-90);
          }
          this.rectangle.setTranslateX((int) this.position.getX());
          velocity.setX(0);
        }
      }
    }

    // Move cube in y direction and check for collisions
    this.position.setY(this.position.getY() + velocity.getY() * dt);
    this.rectangle.setTranslateY((int) this.position.getY());

    for (Block block : neighbourBlocks) {
      if (block != null) {
        if (this.rectangle
            .getBoundsInParent()
            .intersects(block.getRectangle().getBoundsInParent())) {
          jumping = false;

          // If the block was below the cube before collision
          if (velocity.getY() > 0) {
            this.position.setY(block.getY() - rectangle.getHeight() - 1);
            this.setAccelerationAngle(0);
          }
          // If the block was above the cube before collision
          else if (velocity.getY() < 0) {
            this.position.setY(block.getY() + block.getRectangle().getHeight() + 1);
            this.setAccelerationAngle(180);
          }

          this.rectangle.setTranslateY((int) this.position.getY());
          velocity.setY(0);
        }
      }
    }
  }

  /** Makes the cube accelerate to its maximum speed at the beginning of a level. */
  public void initialiseSpeed() {
    this.velocity.setX(size.getX() * blocksPerSecond);
    this.velocity.setY(0);
  }

  /**
   * Rotates the gravitational acceleration vector to the given angle.
   *
   * @param angle compared to the y-axis
   */
  private void setAccelerationAngle(int angle) {
    this.acceleration.setX(Math.sin(Math.toRadians(angle)) * size.getX() * blocksPerSecond);
    this.acceleration.setY(Math.cos(Math.toRadians(angle)) * size.getX() * blocksPerSecond);
    this.accelerationAngle = angle;
  }
}
