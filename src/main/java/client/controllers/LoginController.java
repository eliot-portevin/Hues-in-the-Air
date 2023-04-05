package client.controllers;

import gui.GuiJavaFX;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class LoginController {

  // Grid panes
  public GridPane backgroundPane;
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
}
