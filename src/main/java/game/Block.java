package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block {
  private final Color color;
  private Rectangle rectangle;

  private int x;
  private int y;

  /**
   * Creates a new block
   *
   * @param color the color of the block
   */
  public Block(Color color, int x, int y, int size) {
    this.color = color;
    this.rectangle = new Rectangle(x, y, size, size);
  }

  /** Getter for the color of the block */
  public Color getColor() {
    return color;
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
}
