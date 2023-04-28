package game;

/** Represents a 2D Vector and various operations performed on 2D Vectors. */
public class Vector2D {
  private double xComponent;

  private double yComponent;

  /**
   * Creates a new 2D vector with the given x and y components.
   *
   * @param xComponent the x component of the 2D vector
   * @param yComponent the y component of the 2D vector
   */
  public Vector2D(double xComponent, double yComponent) {
    this.xComponent = xComponent;
    this.yComponent = yComponent;
  }

  /**
   * Returns the x component of the 2D vector.
   *
   * @return the x component of the 2D vector
   */
  public double getX() {
    return this.xComponent;
  }

  /**
   * Returns the y component of the 2D vector.
   *
   * @return the y component of the 2D vector
   */
  public double getY() {
    return this.yComponent;
  }

  /**
   * Sets the x component of the 2D vector.
   *
   * @param xComponent the new x component
   */
  public void setX(double xComponent) {
    this.xComponent = xComponent;
  }

  /**
   * Sets the y component.
   *
   * @param yComponent the new y component
   */
  public void setY(double yComponent) {
    this.yComponent = yComponent;
  }

  /**
   * Adds the new vector to the existing one.
   *
   * @param vector2 the vector to add
   */
  public void addInPlace(Vector2D vector2) {
    this.xComponent += vector2.getX();
    this.yComponent += vector2.getY();
  }

  /**
   * Adds two vectors and returns the new vector.
   *
   * @param vector1 the first vector
   * @param vector2 the second vector
   * @return the new vector
   */
  public static Vector2D add(Vector2D vector1, Vector2D vector2) {
    double newX = vector1.getX() + vector2.getX();
    double newY = vector1.getY() + vector2.getY();
    return new Vector2D(newX, newY);
  }

  /**
   * Multiplies the vector by a scalar and returns the new vector.
   *
   * @param vector the vector to multiply
   * @param scalar the scalar to multiply by
   * @return the new vector
   */
  public static Vector2D multiply(Vector2D vector, double scalar) {
    double newX = vector.getX() * scalar;
    double newY = vector.getY() * scalar;
    return new Vector2D(newX, newY);
  }

  /**
   * Multiplies the vector by a scalar.
   *
   * @param scalar the scalar to multiply by
   * @return the new vector
   */
  public Vector2D multiply(double scalar) {
    return Vector2D.multiply(this, scalar);
  }

  /**
   * Multiplies the vector by a scalar in place.
   *
   * @param scalar the scalar to multiply by
   */
  public void multiplyInPlace(double scalar) {
    this.xComponent *= scalar;
    this.yComponent *= scalar;
  }

  /**
   * Returns true if the actual vector is the same as the other one.
   *
   * @param other the other vector to compare to
   * @return true if the vectors are the same
   */
  public boolean equals(Vector2D other) {
    return this.xComponent == other.xComponent && this.yComponent == other.yComponent;
  }

  /**
   * Return a clone of the vector.
   *
   * @return a clone of the vector
   */
  public Vector2D copy() {
    return new Vector2D(this.xComponent, this.yComponent);
  }

  /**
   * Subtracts two vectors and returns a new vector with the appropriate magnitudes.
   *
   * @param otherVector the vector to subtract from this vector
   * @return the new vector
   */
  public Vector2D subtract(Vector2D otherVector) {
    return new Vector2D(
        this.xComponent - otherVector.xComponent, this.yComponent - otherVector.yComponent);
  }
}
