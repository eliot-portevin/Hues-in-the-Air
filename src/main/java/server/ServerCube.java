package server;

import client.Client;
import game.Vector2D;
import game.Block;
import game.Colours;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Objects;

public class ServerCube {
  // Position, velocity, acceleration
  private final int blocksPerSecond = 6;
  private final double velocity_constant;
  private final double acceleration_constant;

  protected Vector2D position;
  public Vector2D start_position = new Vector2D(0, 0);
  protected Vector2D velocity = new Vector2D(0, 0);
  public Vector2D acceleration = new Vector2D(0, 0);
  private int accelerationAngle = 0;

  private boolean jumping = true;
  private final Object jumpLock = new Object();
  private Color colourCanJump;

  // Cube information
  public int cubeSize;
  protected Rectangle rectangle = new Rectangle();

  // Level information
  private final Pane gameRoot;
  public int blockSize;
  private Vector2D rotationPoint;
  private double y0 = 100000;
  private boolean y0passed;

  public ServerCube(Pane gameRoot, Vector2D position, int cubeSize, int blockSize) {
    // Initialise position, velocity and acceleration
    this.position = position;
    this.velocity_constant = blockSize * blocksPerSecond;
    this.acceleration_constant = blockSize * blocksPerSecond * 4;
    this.setAccelerationAngle(0);

    this.cubeSize = cubeSize;
    this.blockSize = blockSize;

    this.gameRoot = gameRoot;

    spawnCube();
  }

  /** Spawns the cube at the given position with given size and adds it to the gameRoot */
  public void spawnCube() {
    rectangle = new Rectangle(cubeSize, cubeSize);
    this.setPositionTo(position.getX(), position.getY());
    gameRoot.getChildren().add(rectangle);
  }

  /** Sets position of the cube to the given x and y */
  public void setPositionTo(double x, double y) {
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }

  /**
   * Makes the cube jump by setting the velocity of the cube to the opposite of the gravity vector.
   */
  public void jump(Color colour) {
    if (!jumping && colour.equals(colourCanJump)) {
      Vector2D jumpVector =
          new Vector2D(
              Math.sin(Math.toRadians(accelerationAngle)),
              Math.cos(Math.toRadians(accelerationAngle)));

      jumpVector.multiplyInPlace(-blockSize * blocksPerSecond * 2);

      velocity.addInPlace(jumpVector);

      jumping = true;
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
    this.rectangle.setTranslateX(this.position.getX());

    for (Block block : neighbourBlocks) {
      if (block != null) {
        if (this.rectangle
            .getBoundsInParent()
            .intersects(block.getRectangle().getBoundsInParent())) {
          boolean isEdgeCollision = isEdgeCollision(block, true);

          if (!isEdgeCollision) {
            // If the block was to the right of the cube before collision
            if (velocity.getX() > 0) {
              this.position.setX(block.getX() - this.rectangle.getWidth());
              this.setAccelerationAngle(90);
            }
            // If the block was to the left of the cube before collision
            else if (velocity.getX() < 0) {
              this.position.setX(block.getX() + block.getRectangle().getWidth());
              this.setAccelerationAngle(-90);
            }
            this.rectangle.setTranslateX(this.position.getX());

            this.velocity.setX(0);
            jumping = false;

            // Allow the player with this colour to jump
            this.colourCanJump = block.getColour();
          }

          // Check for collision with white block
          if (block.getColour().equals(Colours.WHITE.getHex())) {
            this.resetLevel();
          }
        }
      }
    }

    // Move cube in y direction and check for collisions
    this.position.setY(this.position.getY() + velocity.getY() * dt);
    this.rectangle.setTranslateY(this.position.getY());

    for (Block block : neighbourBlocks) {
      if (block != null) {
        if (this.rectangle
            .getBoundsInParent()
            .intersects(block.getRectangle().getBoundsInParent())) {
          boolean isEdgeCollision = isEdgeCollision(block, false);

          if (!isEdgeCollision) {
            // If the block was below the cube before collision
            if (velocity.getY() > 0) {
              this.position.setY(block.getY() - rectangle.getHeight());
              this.setAccelerationAngle(0);
            }
            // If the block was above the cube before collision
            else if (velocity.getY() < 0) {
              this.position.setY(block.getY() + block.getRectangle().getHeight());
              this.setAccelerationAngle(180);
            }

            this.rectangle.setTranslateY(this.position.getY());

            velocity.setY(0);
            jumping = false;

            // Allow the player with this colour to jump
            this.colourCanJump = block.getColour();
          }

          // Check collision with a white block
          if (block.getColour().equals(Colours.WHITE.getHex())) {
            this.resetLevel();
          }
        }
      }
    }

    // Check whether the cube has passed the ground coordinates, if that is the case, rotate it
    // around the edge
    if (this.jumping) {
      checkForRotation();
    }
  }

  /** Makes the cube accelerate to its maximum speed at the beginning of a level. */
  public void initialiseSpeed() {
    this.velocity.setX(blockSize * blocksPerSecond);
    this.velocity.setY(0);

    this.acceleration.setX(0);
    this.acceleration.setY(cubeSize * blocksPerSecond);
    this.setAccelerationAngle(0);
  }

  /**
   * Rotates the gravitational acceleration vector to the given angle.
   *
   * @param angle compared to the y-axis
   */
  private void setAccelerationAngle(int angle) {
    int angleDifference = accelerationAngle - angle;
    this.accelerationAngle = angle;

    if (!(Math.abs(angleDifference) % 180 == 0)) {
      this.velocity.setX(Math.signum(-this.acceleration.getX()) * velocity_constant);
      this.velocity.setY(Math.signum(-this.acceleration.getY()) * velocity_constant);
    }

    this.acceleration.setX(Math.sin(Math.toRadians(angle)) * acceleration_constant);
    this.acceleration.setY(Math.cos(Math.toRadians(angle)) * acceleration_constant);
  }

  /**
   * The cube has entered in contact with a white cube. It is sent back to the start of the level
   * and the level is reset.
   */
  void resetLevel() {
    this.position.setX(start_position.getX());
    this.position.setY(start_position.getY());
    this.rectangle.setTranslateX(this.position.getX());
    this.rectangle.setTranslateY(this.position.getY());

    this.setAccelerationAngle(0);

    this.velocity.setX(0);
    this.velocity.setY(0);

    ServerGame.getInstance().resetLevel();
  }

  /**
   * A block has collided with a wall. If that collision was completely on the edge, it is ignored.
   *
   * @param block the block that was collided with
   * @param isX whether the collision was in the x direction
   * @return whether the collision was on the edge
   */
  private boolean isEdgeCollision(Block block, Boolean isX) {
    if (isX) {
      return (block.getY() == this.position.getY() + this.cubeSize
          || block.getY() + block.getRectangle().getHeight() == this.position.getY());
    }
    return (block.getX() == this.position.getX() + this.cubeSize
        || block.getX() + block.getRectangle().getWidth() == this.position.getX());
  }

  /**
   * The cube is able to rotate around edges when it is jumping. This is done by taking a "rotation"
   * point at the moment of the jump; if the cube passes the ground coordinates without any
   * collision, i.e. it passes the rotation point, the gravitational acceleration is rotated by 90
   * degrees.
   */
  private void checkForRotation() {}
}
