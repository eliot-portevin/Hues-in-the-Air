package client;

import game.Colours;
import game.Vector2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/** Represents the cube in the game. */
public class Cube {
  /** The position of the cube. */
  protected Vector2D position;
  /** The size of the cube (in pixel). */
  public Vector2D size;
  /** The root on which the cube is drawn. */
  private final Pane gameRoot;
  /** The size of the blocks in the game. */
  public int blockSize;
  /** The rectangle which the cube uses to detect collisions */
  protected Rectangle rectangle = new Rectangle();

  /**
   * Creates a new cube.
   * @param gameRoot the root on which the cube is drawn
   * @param position the position of the cube
   * @param size the size of the cube
   */
  public Cube(Pane gameRoot, Vector2D position, Vector2D size) {
    this.position = position;
    this.size = size;
    this.gameRoot = gameRoot;
    spawnCube();
  }

  /** Spawns the cube at the given position with given size and adds it to the gameRoot */
  public void spawnCube() {
    rectangle = new Rectangle(size.getX(), size.getY());
    this.setPositionTo(position.getX(), position.getY());
    rectangle.setFill(Colours.GREEN.getHex());
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
  }

  /** Called upon collision with a white block, is responsible for the death effects of the cube. */
  public void death() {
    rectangle.setFill(Colours.DARK_GREY.getHex());
  }
}
