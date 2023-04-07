package client;

/**
 * Represents a 2D Vector and various operations performed on 2D Vectors
 */
public class Vector2D {
  private double xComponent;

  private double yComponent;

  public Vector2D(double xComponent, double yComponent) {
    this.xComponent = xComponent;
    this.yComponent = yComponent;
  }

  public double getX() {
    return this.xComponent;
  }

  public double getY() {
    return this.yComponent;
  }

  public void setX(double xComponent) {
    this.xComponent = xComponent;
  }

  public void setY(double yComponent) {
    this.yComponent = yComponent;
  }

  public void addInPlace(Vector2D vector2) {
    this.xComponent += vector2.getX();
    this.yComponent += vector2.getY();
  }

  public static Vector2D add(Vector2D vector1, Vector2D vector2) {
    double newX = vector1.getX() + vector2.getX();
    double newY = vector1.getY() + vector2.getY();
    return new Vector2D(newX, newY);
  }

  public boolean equals(Vector2D other) {
    return this.xComponent == other.xComponent && this.yComponent == other.yComponent;
  }
}
