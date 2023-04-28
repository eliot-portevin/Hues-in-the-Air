package server;

import game.Block;
import game.Colours;
import game.GameConstants;
import game.Vector2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** The cube instance which the server works with. */
public class ServerCube {
  // Position, velocity, acceleration
  final double velocity_constant;
  final double acceleration_constant;

  /** The current position of the cube. */
  protected Vector2D position;
  /** The spawn position of the cube. */
  public Vector2D start_position = new Vector2D(0, 0);
  /** The velocity which the cube currently has. */
  protected Vector2D velocity = new Vector2D(0, 0);

  private final double maxVelocity;
  /** The acceleration which the cube is currently experiencing. */
  public Vector2D acceleration = new Vector2D(0, 0);

  int accelerationAngle = 0;

  private boolean jumping = true;
  private boolean canRotate = false;
  private Color colourCanJump;

  // Cube information
  /** The size of the cube in pixels. */
  public int cubeSize;
  /** The rectangle which represents the cube. Used to detect collisions. */
  protected Rectangle rectangle = new Rectangle();

  // Level information
  private final Pane gameRoot;
  /** The size of a block in pixels. */
  public int blockSize;

  private Vector2D rotationPoint;

  /**
   * Creates a new cube.
   *
   * @param gameRoot A virtual game root which the cube and blocks will be added to
   * @param position The position of the cube
   */
  public ServerCube(Pane gameRoot, Vector2D position) {
    // Initialise position, velocity and acceleration
    this.position = position;
    this.velocity_constant = GameConstants.CUBE_VELOCITY.getValue();
    this.maxVelocity = GameConstants.CUBE_MAX_VELOCITY.getValue();
    this.acceleration_constant = GameConstants.CUBE_ACCELERATION.getValue();
    this.setAccelerationAngle(GameConstants.DEFAULT_ACCELERATION_ANGLE.getValue());

    this.cubeSize = GameConstants.CUBE_SIZE.getValue();
    this.blockSize = GameConstants.BLOCK_SIZE.getValue();

    this.gameRoot = gameRoot;

    spawnCube();
  }

  /** Spawns the cube at the given position with given size and adds it to the gameRoot */
  public void spawnCube() {
    rectangle = new Rectangle(cubeSize, cubeSize);
    this.setPositionTo(position.getX(), position.getY());
    gameRoot.getChildren().add(rectangle);
  }

  /**
   * Sets position of the cube to the given x and y.
   *
   * @param x the x position to set the cube to
   * @param y the y position to set the cube to
   */
  public void setPositionTo(double x, double y) {
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
    this.position.setX(x);
    this.position.setY(y);
  }

  /**
   * Returns the position vector of the cube
   *
   * @return position of the cube
   */
  public Vector2D getPosition() {
    return position;
  }

  /**
   * Returns the velocity vector of the cube
   *
   * @return velocity of the cube
   */
  public Vector2D getVelocity() {
    return velocity;
  }

  /**
   * Returns the acceleration vector of the cube
   *
   * @return acceleration of the cube
   */
  public Vector2D getAcceleration() {
    return acceleration;
  }

  /**
   * Makes the cube jump by setting the velocity of the cube to the opposite of the gravity vector.
   * Sets the rotation point to be halfway to the landing point in the middle of a block.
   *
   * @param colour the colour of the player who requested a jump
   */
  public void jump(Color colour) {
    if (!jumping && colour.equals(colourCanJump)) {
      // Calculate point around which the cube will rotate if necessary (a jump lasts for one
      // second)
      rotationPoint = new Vector2D(position.getX(), position.getY());
      rotationPoint.addInPlace(velocity.multiply(0.5));
      rotationPoint.addInPlace(
          new Vector2D(
              Math.signum(acceleration.getX()) * blockSize,
              Math.signum(acceleration.getY()) * blockSize));

      // Adjust the speed of the cube for it to jump
      Vector2D jumpVector =
          new Vector2D(
              Math.sin(Math.toRadians(accelerationAngle)),
              Math.cos(Math.toRadians(accelerationAngle)));

      jumpVector.multiplyInPlace(-maxVelocity);

      velocity.addInPlace(jumpVector);

      // Don't allow the cube to jump again until it has landed
      jumping = true;
      canRotate = true;
    }
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
          // Checks for collision with a coin
          this.checkCoinCollision(block);

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
              this.setAccelerationAngle(270);
            }
            this.rectangle.setTranslateX(this.position.getX());

            this.velocity.setX(0);
            jumping = false;
            canRotate = false;

            // Allow the player with this colour to jump
            this.colourCanJump = block.getColour();
          }

          // Check for collision with white block
          if (block.getColour().equals(Colours.WHITE.getHex())) {
            this.resetMovement();
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
          // Checks for collision with a coin
          this.checkCoinCollision(block);

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
            canRotate = false;

            // Allow the player with this colour to jump
            this.colourCanJump = block.getColour();
          }

          // Check collision with a white block
          if (block.getColour().equals(Colours.WHITE.getHex())) {
            this.resetMovement();
          }
        }
      }
    }

    // Check whether the cube has passed the ground coordinates, if that is the case, rotate it
    // around the edge
    if (this.canRotate) {
      checkForRotation();
    }
  }

  /** Makes the cube accelerate to its maximum speed at the beginning of a level. */
  public void initialiseSpeed() {
    this.velocity.setX(this.velocity_constant);
    this.velocity.setY(0);

    this.setAccelerationAngle(0);
  }

  /**
   * Rotates the gravitational acceleration vector to the given angle without adjusting the cube
   * velocity. Called to rotate the cube around an edge as opposed to reset the velocity after a
   * collision.
   */
  void onlySetAccelerationAngle(int angle) {
    this.accelerationAngle = angle;

    this.acceleration.setX(Math.sin(Math.toRadians(angle)) * acceleration_constant);
    this.acceleration.setY(Math.cos(Math.toRadians(angle)) * acceleration_constant);
  }

  /**
   * Rotates the gravitational acceleration vector to the given angle and adjusts the cube velocity.
   * Called when the cube has collided with a block.
   *
   * @param angle compared to the y-axis
   */
  void setAccelerationAngle(int angle) {
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
  void resetMovement() {
    this.position.setX(start_position.getX());
    this.position.setY(start_position.getY());
    this.rectangle.setTranslateX(this.position.getX());
    this.rectangle.setTranslateY(this.position.getY());

    this.setAccelerationAngle(0);

    this.velocity.setX(0);
    this.velocity.setY(0);

    if (ServerGame.getInstance() != null) ServerGame.getInstance().resetLevel();
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
  private void checkForRotation() {
    if (this.rotationPoint == null) {
      // The cube isn't actually jumping (e.g. at the beginning of the level)
      return;
    }

    if (this.accelerationAngle == 0) {
      if (this.position.getY() > this.rotationPoint.getY()) {
        this.onlySetAccelerationAngle(180);
        this.velocity.setX(-this.velocity.getX());
        this.canRotate = false;
      }
    } else if (this.accelerationAngle == 90) {
      if (this.position.getX() > this.rotationPoint.getX()) {
        this.onlySetAccelerationAngle(270);
        this.velocity.setY(-this.velocity.getY());
        this.canRotate = false;
      }
    } else if (this.accelerationAngle == 180) {
      if (this.position.getY() < this.rotationPoint.getY()) {
        this.onlySetAccelerationAngle(0);
        this.velocity.setX(-this.velocity.getX());
        this.canRotate = false;
      }
    } else if (this.accelerationAngle == 270) {
      if (this.position.getX() < this.rotationPoint.getX()) {
        this.onlySetAccelerationAngle(90);
        this.velocity.setY(-this.velocity.getY());
        this.canRotate = false;
      }
    }
  }

  /**
   * Checks whether the player is currently colliding with the coin (end of the level). If that is
   * the case, the next level is loaded. Called from {@link ServerCube#move(Block[], double)}.
   */
  private void checkCoinCollision(Block block) {
    if (block.getColour().equals(Colours.TRANSPARENT.getHex())) {
      ServerGame.getInstance().nextLevel();
    }
  }
}
