package game;

import javafx.scene.paint.Color;

/** The colours used in the game. */
public enum Colours {
  /** The colour black */
  BLACK("#363636"),
  /** The colour white */
  WHITE("#ffffff"),
  /** The colour yellow */
  YELLOW("#fccf78"),
  /** The colour pink */
  PINK("#f57dc6"),
  /** The colour violet */
  VIOLET("#9d59db"),
  /** The colour dark blue */
  BLUE1("#6aa1f7"),
  /** The colour light blue */
  BLUE2("#b3d5f2"),
  /** The colour green */
  GREEN("#9ae6ae"),
  /** The colour grey */
  GREY("#777777"),
  /** The colour dark grey */
  DARK_GREY("#464646");

  private final String hex;

  Colours(String hex) {
    this.hex = hex;
  }

  /** Returns the hex of the colour.
   * @return a colour object with the value of the hex
   * */
  public Color getHex() {
    return Color.valueOf(this.hex);
  }
}
