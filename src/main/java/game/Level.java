package game;

import java.util.ArrayList;
import java.util.Optional;

import client.LevelReader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/** The level class which loads a level from a string and handles the logic for the level. */
public class Level {
  // Grid of blocks
  private Block[][] grid;
  /** The width of a block. */
  public int blockWidth;
  /** The position at which the player spawns. */
  public int[] playerSpawnIdx = new int[2];

  private final ArrayList<Block> criticalBlocks = new ArrayList<>();

  // Pane on which the level is drawn
  Pane gameRoot;

  /**
   * Creates a new level.
   * @param levelPath the path to the level
   * @param blockWidth the width of a block
   * @param gameRoot the pane on which the level is drawn
   */
  public Level(String levelPath, int blockWidth, Pane gameRoot) {
    this.blockWidth = blockWidth;
    this.gameRoot = gameRoot;

    String levelString = LevelReader.readLevel(levelPath);
    loadLevel(levelString);
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
          case '2' -> this.grid[i][j] = new Block(null, j * blockWidth, i * blockWidth, blockWidth);
          case '3' -> {
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
   *
   * @param x The x position of the block
   * @param y The y position of the block
   *
   * @return The nine blocks neighbouring the block at the given position
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

  /**
   * Iterates through the blocks of the level and sets their colours. If a block hasn't been
   * coloured yet, a colour is randomly chosen and set to it. Its neighbours (up, down, left, right)
   * are there coloured with the same colour.
   *
   * @param colours The colours that can be used to colour the blocks
   */
  public void setNeighbourColours(ArrayList<Color> colours) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        Optional<Block> block = Optional.ofNullable(grid[i][j]);
        if (block.isPresent()) {
          if (block.get().getColour() == null) {
            // Get random colour
            Color colour = colours.get((int) Math.floor(Math.random() * colours.size()));

            // Set block colour (and adjacent blocks recursively)
            setNeighbourColours(j, i, colour);

            // Add block to critical blocks in order for it to be sent to client
            criticalBlocks.add(block.get());
          }
        }
      }
    }
  }

  /**
   * Sets the colours of the blocks in the level. The blocks are coloured in a way that no two
   * adjacent blocks have the same colour.
   *
   * @param xIdx the column index of the block
   * @param yIdx the row index of the block
   * @param colour the colour to set the block to
   */
  public void setNeighbourColours(int xIdx, int yIdx, Color colour) {
    // Recursion return statement
    boolean indexOutOfBounds =
        xIdx < 0 || xIdx >= grid[0].length || yIdx < 0 || yIdx >= grid.length;
    if (indexOutOfBounds) return;
    if (grid[yIdx][xIdx] == null) return;
    if (grid[yIdx][xIdx].getColour() != null) return;

    grid[yIdx][xIdx].setColour(colour);

    int[][] neighbourIndexes = {
      {xIdx - 1, yIdx}, {xIdx + 1, yIdx}, {xIdx, yIdx - 1}, {xIdx, yIdx + 1}
    };

    setNeighbourColours(xIdx - 1, yIdx, colour);
    setNeighbourColours(xIdx + 1, yIdx, colour);
    setNeighbourColours(xIdx, yIdx - 1, colour);
    setNeighbourColours(xIdx, yIdx + 1, colour);
  }

  /**
   * Returns the blocks in the level which "begin" a platform. If the methods {@link
   * #setNeighbourColours(int, int, Color)} are called on these blocks, the whole level will be
   * coloured.
   *
   * @return the critical blocks
   */
  public ArrayList<Block> getCriticalBlocks() {
    return criticalBlocks;
  }
}
