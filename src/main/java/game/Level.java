package game;

import client.LevelData;
import gui.Colours;
import javafx.scene.layout.Pane;

public class Level {
  // Grid of blocks
  private Block[][] grid;
  public int blockWidth;
  public int[] playerSpawnIdx = new int[2];

  // Pane on which the level is drawn
  Pane gameRoot;

  public Level(String difficulty, int blockWidth, Pane gameRoot) {
    this.blockWidth = blockWidth;
    this.gameRoot = gameRoot;

    loadLevel(String.join("\n", LevelData.Level1));
  }

  /**
   * Gets a level as a string input and loads it into the grid. 0 = no block, 1 = white block, 2 =
   * coloured block.
   *
   * @param levelData The level data as a string with different lines separated by a newline
   */
  private void loadLevel(String levelData) {
    String[] gridLines = levelData.split("\n");

    this.grid = new Block[gridLines.length][gridLines[0].length()];

    for (int i = 0; i < gridLines.length; i++) {
      for (int j = 0; j < gridLines[i].length(); j++) {
        if (j > gridLines[0].length()) throw new IllegalStateException("Invalid level data");

        switch (gridLines[i].charAt(j)) {
          case '0' -> this.grid[i][j] = null;
          case '1' -> this.grid[i][j] =
              new Block(Colours.WHITE.getHex(), j * blockWidth, i * blockWidth, blockWidth);
          case '2' -> this.grid[i][j] =
              new Block(Colours.PINK.getHex(), j * blockWidth, i * blockWidth, blockWidth);
          case '7' -> {
            this.playerSpawnIdx[0] = j;
            this.playerSpawnIdx[1] = i;
          }
        }
      }
    }

    for (Block[] line : this.grid) {
      for (Block block : line) {
        if (block != null) {
          gameRoot.getChildren().add(block.getRectangle());

          // Update the x and y positions of block to access them later in collision checks
          block.setX((int) block.getRectangle().getBoundsInParent().getMinX());
          block.setY((int) block.getRectangle().getBoundsInParent().getMinY());
        }
      }
    }
  }

  /**
   * Returns the nine blocks neighbouring a position.
   */
  public Block[] getNeighbourBlocks(double x, double y) {
    int xIndex = (int) Math.floor(x / blockWidth);
    int yIndex = (int) Math.floor(y / blockWidth);

    Block[] neighbours = new Block[9];

    for (int i = -1; i < 2; i++) {
      for (int j = -1; j < 2; j++) {
        try {
          neighbours[(i + 1) * 3 + j + 1] = grid[yIndex + i][xIndex + j];
        } catch (ArrayIndexOutOfBoundsException e) {
          neighbours[(i + 1) * 3 + j + 1] = null;
        }
      }
    }
    return neighbours;
  }
}
