package server;

import game.Block;
import game.Colours;
import game.Cube;
import game.Vector2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * The server version of a cube.
 * Used to handle the logic of the cube on the server side.
 */
public class ServerCube extends Cube {
  /** Lock so that the nextLevel method is only called once.*/
  private boolean nextLevelCalled = false;

  /**
   * Creates a cube.
   *
   * @param gameRoot The pane which the cube will be moving on
   * @param spawnPosition The position which the cube will spawn at
   */
  public ServerCube(final Pane gameRoot, final Vector2D spawnPosition) {
    super(gameRoot, spawnPosition);
  }

  /**
   * Checks whether the player is currently colliding with the coin.
   * (end of the level) If that is the case, the next level is loaded.
   * Called from {@link ServerCube#move(Block[], double)}.
   *
   * @param block The block which might be a coin
   */
  @Override
  public void checkCoinCollision(final Block block) {
    if (block.getColour().equals(Colours.TRANSPARENT.getHex())
            && !nextLevelCalled) {
      nextLevelCalled = true;
      ServerGame.getInstance().nextLevel();
    }
  }

  /**
   * The cube has entered in contact with a white cube.
   * Its position, velocity and acceleration are reset.
   * The game instance is also informed of this event
   * so that a life can be deducted. See {@link ServerGame#die()}.
   */
  @Override
  public void die() {
    if (ServerGame.getInstance() != null)  {
      ServerGame.getInstance().die();
    }
  }

  /**
   * Calls the super method to jump. If a jump has successfully been performed,
   * all clients in the game are informed of the jump (the coordinates
   * of the rotation point). See {@link ServerGame#jumpUpdate()}.
   *
   * @param colour the colour of the player who requested a jump
   */
  @Override
  public void jump(final Color colour) {
    super.jump(colour);

    if (jumping) {
      ServerGame.getInstance().jumpUpdate();
    }
  }
}
