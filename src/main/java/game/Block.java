package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block {
  private final Color colour;
  private Rectangle rectangle;

  private int x;
  private int y;

  /**
   * Creates a new block
   *
   * @param colour the color of the block
   */
  public Block(Color colour, int x, int y, int size) {
    this.colour = colour;
    this.rectangle = new Rectangle(x, y, size, size);
    if (colour != null) {
      this.rectangle.setFill(colour);
    }
  }

  /** Getter for the color of the block */
  public Color getColour() {
    return colour;
  }

  /**
   * Checks if the block collides with a rectangle
   *
   * @param rect the rectangle to check collision with
   * @return true if the block collides with the rectangle
   */
  public boolean collideRect(Rectangle rect) {
    return rectangle.getBoundsInParent().intersects(rect.getBoundsInParent());
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
   * @param colour the colour of the block
   */
  public void setColour(Color colour) {
    this.rectangle.setFill(colour);
  }
}
