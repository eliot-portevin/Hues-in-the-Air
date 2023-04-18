package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Block {
  private final Color color;
  Rectangle rectangle;

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
   * @param rect the rectangle to check collision with
   * @return true if the block collides with the rectangle
   */
  public boolean collideRect(Rectangle rect) {
    return rectangle.getBoundsInParent().intersects(rect.getBoundsInParent());
  }
}
