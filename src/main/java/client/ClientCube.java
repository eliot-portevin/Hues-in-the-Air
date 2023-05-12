package client;

import game.Block;
import game.Colours;
import game.Cube;
import game.Vector2D;
import javafx.scene.layout.Pane;

/** Represents the cube in the game. */
public class ClientCube extends Cube {
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
}
