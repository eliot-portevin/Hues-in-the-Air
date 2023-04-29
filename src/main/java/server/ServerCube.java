package server;

import game.Block;
import game.Colours;
import game.Cube;
import game.Vector2D;
import javafx.scene.layout.Pane;

public class ServerCube extends Cube {
  /**
   * Creates a cube.
   *
   * @param gameRoot The pane which the cube will be moving on
   * @param spawnPosition The position which the cube will spawn at
   */
  public ServerCube(Pane gameRoot, Vector2D spawnPosition) {
    super(gameRoot, spawnPosition);
  }

  /**
   * Checks whether the player is currently colliding with the coin (end of the level). If that is
   * the case, the next level is loaded. Called from {@link ServerCube#move(Block[], double)}.
   *
   * @param block The block which might be a coin
   */
  @Override
  public void checkCoinCollision(Block block) {
    if (block.getColour().equals(Colours.TRANSPARENT.getHex())) {
      ServerGame.getInstance().nextLevel();
    }
  }

  /**
   * The cube has entered in contact with a white cube. Its position, velocity and acceleration are
   * reset. The game instance is also informed of this event so that a life can be deducted. See
   * {@link ServerGame#die()}.
   */
  @Override
  public void die() {
    if (ServerGame.getInstance() != null) ServerGame.getInstance().die();
  }
}
