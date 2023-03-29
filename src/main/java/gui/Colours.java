package gui;

import javafx.scene.paint.Color;public enum Colours {
  BLACK("#363636"),
  WHITE("#ffffff"),
  YELLOW("#fccf78"),
  PINK("#f57dc6"),
  VIOLET("#9d59db"),
  BLUE1("#6aa1f7"),
  BLUE2("#b3d5f2"),
  GREEN("#9ae6ae"),
  GREY("#777777"),
  DARK_GREY("#464646");

  private String hex;

  Colours(String hex) {
    this.hex = hex;
  }

  public Color getHex() {
    return Color.valueOf(this.hex);
  }
}
