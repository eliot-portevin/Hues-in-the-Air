package game;

/** The constants used in the game. */
public enum GameConstants {
  BLOCKS_PER_SECOND(8),
  CUBE_SIZE(30),
  GRID_SIZE(50),
  VELOCITY_CONSTANT(GRID_SIZE.value * BLOCKS_PER_SECOND.value),
  ACCELERATION_CONSTANT(GRID_SIZE.value * BLOCKS_PER_SECOND.value * 4);

  private final int value;
  GameConstants(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
