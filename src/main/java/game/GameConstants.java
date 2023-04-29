package game;

/** The constants used in the game. */
public enum GameConstants {
  /** The number of lives which players have at the start of the game. */
  DEFAULT_LIVES(3),
  /** The number of lives which players gain after completing an easy level. */
  LIFE_GAIN_EASY(1),
  /** The number of lives which players gain after completing a medium level. */
  LIFE_GAIN_MEDIUM(2),
  /** The number of lives which players gain after completing a hard level. */
  LIFE_GAIN_HARD(3),
  /** The maximum number of lives which players can have. */
  MAX_LIVES(10),
  /** The number of blocks which the cube moves by in a second. */
  BLOCKS_PER_SECOND(8),
  /** The size of the cube in px. */
  CUBE_SIZE(30),
  /** The size of a block in px. */
  BLOCK_SIZE(50),
  /** The cube's velocity in px/s. */
  CUBE_VELOCITY(BLOCK_SIZE.value * BLOCKS_PER_SECOND.value),
  /** The cube's maximum velocity in px/s (jump velocity). */
  CUBE_MAX_VELOCITY(CUBE_VELOCITY.getValue() * 2),
  /** The cube's acceleration in px/s^2. */
  CUBE_ACCELERATION(BLOCK_SIZE.value * BLOCKS_PER_SECOND.value * 4),
  /** The default direction in which the gravity acts. */
  DEFAULT_ACCELERATION_ANGLE(0);

  /** The value of the constant. */
  private final int value;

  /**
   * Initialises the constant.
   * @param value The value of the constant.
   */
  GameConstants(int value) {
    this.value = value;
  }

  /**
   * Returns the value of the constant.
   * @return The value of the constant.
   */
  public int getValue() {
    return value;
  }
}
