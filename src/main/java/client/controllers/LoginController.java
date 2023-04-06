package client.controllers;

import gui.GuiJavaFX;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.beans.binding.Bindings;

public class LoginController {

  // Grid panes
  public GridPane backgroundPane;
  public GridPane titlePane;
  public HBox hboxPort;
  public HBox hboxLogin;

  // Labels
  public Label labelUsername;
  public Label labelIp;
  public Label labelPort;

  // Title
  public Label titleHues;
  public Label titleThe;
  public Label titleIn;
  public Label titleAir;

  // Connect to server button
  @FXML private Button button;

  // Text fields
  @FXML private TextField textUsername;
  @FXML private TextField textIp;
  @FXML private TextField textPort;

  @FXML
  public void initialize() {
    // Change text field when <Tab> or <Enter> is clicked
    this.setTabBehaviour();

    // Set behaviour for the login button
    this.setButtonBehaviour();

    // Automatically change font sizes with window size
    this.setFontBehaviour();
  }

  /**
   * Sets the button behaviour for the login screen. When the user presses the button, the contents
   * of all text fields will be checked. If they are all completed in a valid way, the program will
   * attempt to connect to the server with the given information.
   */
  private void setButtonBehaviour() {
    // hover sound for button
    button.setOnMouseEntered(e -> GuiJavaFX.clickSound());

    this.button.setOnAction(
        e -> {
          String username = textUsername.getText();
          String ip = textIp.getText();
          String port = textPort.getText();
        });
  }

  /**
   * Sets the tab behaviour for the login screen. When the user presses <Tab> or <Enter> the focus
   * will be set to the next text field.
   */
  private void setTabBehaviour() {
    textUsername.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB") || e.getCode().toString().equals("ENTER")) {
            textIp.requestFocus();
          }
        });
    textIp.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB") || e.getCode().toString().equals("ENTER")) {
            textPort.requestFocus();
          }
        });
    textPort.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB") || e.getCode().toString().equals("ENTER")) {
            button.requestFocus();
          }
        });
    button.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB") || e.getCode().toString().equals("ENTER")) {
            textUsername.requestFocus();
          }
        });
  }

  /**
   * Binds the font size property of labels and title to the width of the window. This way the font
   * size will automatically change with the window size. The calculations are purely based on
   * trial-and-error, they are not based on some meaningful formula.
   */
  private void setFontBehaviour() {
    titleHues.styleProperty().bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(5)));
    titleThe.styleProperty().bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(10)));
    titleIn.styleProperty().bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(10)));
    titleAir.styleProperty().bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(5)));

    labelUsername.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));
    labelIp.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));
    labelPort.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));

    textUsername.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(25)));
    textIp.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(25)));
    textPort.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(25)));

    button.styleProperty().bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));

    // Disable focus on text fields and button
    textUsername.setFocusTraversable(false);
    textIp.setFocusTraversable(false);
    textPort.setFocusTraversable(false);
    button.setFocusTraversable(false);
  }
}
