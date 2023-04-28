package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/** Represents a block in the game */
public class Block {
  private Color colour;
  private Rectangle rectangle;

  private int x;
  private int y;
  private int xIdx;
  private int yIdx;

  /**
   * Creates a new block
   *
   * @param colour the color of the block
   * @param x the x position of the block
   * @param y the y position of the block
   * @param size the size of the block
   */
  public Block(Color colour, int x, int y, int size) {
    this.colour = colour;
    this.rectangle = new Rectangle(x, y, size, size);
    if (colour != null) {
      this.rectangle.setFill(colour);
    }

    this.x = x;
    this.y = y;
    this.xIdx = x / size;
    this.yIdx = y / size;
  }

  /**
   * Getter for the color of the block
   *
   * @return the color of the block
   */
  public Color getColour() {
    return colour;
  }

  /**
   * @return the rectangle of the block
   */
  public Rectangle getRectangle() {
    return rectangle;
  }

  /**
   * Returns the x position of the block
   *
   * @return the x position of the block
   */
  public int getX() {
    return x;
  }

  /**
   * Returns the y position of the block
   *
   * @return the y position of the block
   */
  public int getY() {
    return y;
  }

  /**
   * Sets the x position of the block
   *
   * @param x the x position of the block
   */
  public void setX(int x) {
    this.x = x;
  }

  /**
   * Sets the y position of the block
   *
   * @param y the y position of the block
   */
  public void setY(int y) {
    this.y = y;
  }

  /**
   * Sets the colour of the block
   *
   * @param colour the colour of the block
   */
  public void setColour(Color colour) {
    this.rectangle.setFill(colour);
    this.colour = colour;
  }

  /**
   * Returns the index of the block in the level grid
   *
   * @return the index of the block in the level grid
   */
  public int[] getIndex() {
    return new int[] {xIdx, yIdx};
  }
}
