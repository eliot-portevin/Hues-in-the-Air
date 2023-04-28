package game;

/** The constants used in the game. */
public enum GameConstants {
  BLOCKS_PER_SECOND(8),
  CUBE_SIZE(30),
  BLOCK_SIZE(50),
  CUBE_VELOCITY(BLOCK_SIZE.value * BLOCKS_PER_SECOND.value),
  CUBE_MAX_VELOCITY(CUBE_VELOCITY.getValue() * 2),
  CUBE_ACCELERATION(BLOCK_SIZE.value * BLOCKS_PER_SECOND.value * 4),
  DEFAULT_ACCELERATION_ANGLE(0);

  private final int value;
  GameConstants(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
