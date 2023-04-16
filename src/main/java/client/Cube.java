package client;

import gui.Colours;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Cube {
  protected Vector2D position;
  public Vector2D size;
  private final Pane gameRoot;
  public ArrayList<Node> platforms;
  public ArrayList<Node> death_platforms;
  public int gridSize;
  protected Rectangle rectangle = new Rectangle();


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
   * Sets position of the cube to the given x and y
   */
  public void setPositionTo(double x, double y) {
    this.rectangle.setTranslateX(x);
    this.rectangle.setTranslateY(y);
  }


  /**
   * Called upon collision with a white block, is responsible for the death effects of the cube.
   */
  public void death() {
    rectangle.setFill(Colours.DARK_GREY.getHex());
  }

}
