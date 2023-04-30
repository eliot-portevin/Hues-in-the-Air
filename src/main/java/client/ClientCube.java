package client;

import game.Block;
import game.Colours;
import game.Cube;
import game.Vector2D;
import javafx.scene.layout.Pane;

/** Represents the cube in the game. */
public class ClientCube extends Cube {
  public boolean rotating = false;
  private boolean clockwise = true;
  /**
   * Creates a cube.
   *
   * @param gameRoot The pane which the cube will be moving on
   * @param spawnPosition The position which the cube will spawn at
   */
  public ClientCube(Pane gameRoot, Vector2D spawnPosition) {
    super(gameRoot, spawnPosition);
    this.rectangle.setFill(Colours.WHITE.getHex());
  }

  /**
   * Abstraction of the collision check between the cube and the coin at the end of the level.
   *
   * @param block The block which might be a coin
   */
  @Override
  public void checkCoinCollision(Block block) {}

  /** Abstraction of the death method. Called when the cube has collided with a white block. */
  @Override
  public void die() {}

  /**
   * Calls the cube move function and rotates the cube if it is jumping. The move function moves the
   * cube and checks for collisions with the blocks in its vicinity.
   *
   * @param neighbourBlocks the blocks to check for collisions with
   * @param dt the time since the last frame in seconds
   */
  @Override
  public void move(Block[] neighbourBlocks, double dt) {
    super.move(neighbourBlocks, dt);

    rotateCube(dt);
  }

  /**
   * If the cube is jumping, rotate the cube by a little amount, so it lands flat. If it has landed
   * but isn't flat yet, make it rotate towards flat.
   *
   * @param dt The time since the last frame
   */
  private void rotateCube(double dt) {
    if (!jumping) {
      rotating = false;
      clockwise = signum(this.getVelocity().getX())
          == signum(Math.cos(Math.toRadians(this.accelerationAngle)))
          && signum(-this.getVelocity().getY())
          == signum(Math.sin(Math.toRadians(this.accelerationAngle)));
      /*
      System.out.println(signum(this.getVelocity().getX()));
      System.out.println(signum(Math.cos(Math.toRadians(this.accelerationAngle))));
      System.out.println(signum(-this.getVelocity().getY()));
      System.out.println(signum(Math.sin(Math.toRadians(this.accelerationAngle))));
      System.out.println("--------------------");
      */
    }

    if (rotating) {
      // Rotate the cube
      if (clockwise) {
        this.rectangle.setRotate(this.rectangle.getRotate() + 180 * dt);
      } else {
        this.rectangle.setRotate(this.rectangle.getRotate() - 180 * dt);
      }
    } else {
      this.rectangle.setRotate(0);
    }
  }

  /**
   * Returns whether a value is positive, negative or zero. Better than Math.signum because it
   * returns 0 if the value is 0.
   *
   * @param value The value to check
   * @return 1 if the value is positive, -1 if it is negative and 0 if it is 0
   */
  private int signum(double value) {
    if (Math.abs(value) < 1e-4) return 0;
    if (value > 0) return 1;
    return -1;
  }
}
