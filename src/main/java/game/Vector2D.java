package game;

/** Represents a 2D Vector and various operations performed on 2D Vectors */
public class Vector2D {
  private double xComponent;

  private double yComponent;

  public Vector2D(double xComponent, double yComponent) {
    this.xComponent = xComponent;
    this.yComponent = yComponent;
  }

  /** returns the x component of the 2D vector */
  public double getX() {
    return this.xComponent;
  }

  /** returns the y component of the 2D vector */
  public double getY() {
    return this.yComponent;
  }

  /** sets the x component */
  public void setX(double xComponent) {
    this.xComponent = xComponent;
  }

  /** sets the y component */
  public void setY(double yComponent) {
    this.yComponent = yComponent;
  }

  /** Adds the new vector to the existing one */
  public void addInPlace(Vector2D vector2) {
    this.xComponent += vector2.getX();
    this.yComponent += vector2.getY();
  }

  /** Adds two vectors and returns the new vector */
  public static Vector2D add(Vector2D vector1, Vector2D vector2) {
    double newX = vector1.getX() + vector2.getX();
    double newY = vector1.getY() + vector2.getY();
    return new Vector2D(newX, newY);
  }

  /** Multiplies the vector by a scalar and returns the new vector */
  public static Vector2D multiply(Vector2D vector, double scalar) {
    double newX = vector.getX() * scalar;
    double newY = vector.getY() * scalar;
    return new Vector2D(newX, newY);
  }

  public Vector2D multiply(double scalar) {
    return Vector2D.multiply(this, scalar);
  }

  /**
   * Multiplies the vector by a scalar in place
   * @param scalar the scalar to multiply by
   */
  public void multiplyInPlace(double scalar) {
    this.xComponent *= scalar;
    this.yComponent *= scalar;
  }

  /**
   * Returns true if the actual vector is the same as the other one
   *
   * @param other the other vector to compare to
   * @return true if the vectors are the same
   */
  public boolean equals(Vector2D other) {
    return this.xComponent == other.xComponent && this.yComponent == other.yComponent;
  }

  /**
   * Return an exact copy of the vector.
   * @return an exact copy of the vector
   */
  public Vector2D clone() {
    return new Vector2D(this.xComponent, this.yComponent);
  }
}
