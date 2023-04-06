package client.controllers;

import client.Client;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.beans.binding.Bindings;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Objects;

public class LoginController {

  // Grid panes
  public GridPane backgroundPane;
  public GridPane titlePane;
  public Pane errorPane;
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
  public Button button;

  // Text fields
  public TextField textUsername;
  public TextField textIp;
  public TextField textPort;

  // Error message
  public Label errorMessage;
  public FadeTransition errorTransition;

  @FXML
  public void initialize() {
    // Change text field when <Tab> or <Enter> is clicked
    this.setTabBehaviour();

    // Set behaviour for the login button
    this.setButtonBehaviour();

    // Automatically change font sizes with window size
    this.setFontBehaviour();

    // Initialise error message
    this.setErrorMessage();
  }

  private void setErrorMessage() {
    errorMessage.styleProperty().bind(Bindings.concat("-fx-font-size: ", errorPane.widthProperty().divide(30)));
    errorTransition = new FadeTransition(Duration.millis(5000), errorPane);
    errorTransition.setFromValue(1.0);
    errorTransition.setToValue(0.0);
    errorTransition.setCycleCount(1);
    errorTransition.setAutoReverse(false);
  }

  /**
   * Sets the button behaviour for the login screen. When the user presses the button, the contents
   * of all text fields will be checked. If they are all completed in a valid way, the program will
   * attempt to connect to the server with the given information.
   */
  private void setButtonBehaviour() {
    // hover sound for button
    button.setOnMouseEntered(e -> Client.getInstance().clickSound());

    this.button.setOnAction(
        e -> Client.getInstance()
            .connect(textUsername.getText(), textIp.getText(), textPort.getText()));
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
          if (e.getCode().toString().equals("TAB")) {
            button.requestFocus();
          }
          else if (e.getCode().toString().equals("ENTER")) {
            button.fire();
          }
        });
    button.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB")) {
            textUsername.requestFocus();
          }
          else if (e.getCode().toString().equals("ENTER")) {
            button.fire();
          }
        });
  }

  /**
   * Binds the font size property of labels and title to the width of the window. This way the font
   * size will automatically change with the window size. The calculations are purely based on
   * trial-and-error, they are not based on some meaningful formula.
   */
  private void setFontBehaviour() {
    titleHues
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(5)));
    titleThe
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(10)));
    titleIn
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(10)));
    titleAir
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", titlePane.widthProperty().divide(5)));

    labelUsername
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));
    labelIp
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));
    labelPort
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));

    textUsername
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(25)));
    textIp
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(25)));
    textPort
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(25)));

    button
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", hboxLogin.widthProperty().divide(22)));

    // Disable focus on text fields and button
    textUsername.setFocusTraversable(false);
    textIp.setFocusTraversable(false);
    textPort.setFocusTraversable(false);
    button.setFocusTraversable(false);
  }

  /**
   * Fills the text fields with the given arguments. The arguments are given in the following order:
   * username, ip, port.
   *
   * @param args The arguments to fill the text fields with.
   */
  public void fillFields(String[] args) {
    TextField[] textFields = {textIp, textPort, textUsername};

    for (int i=0; i<args.length; i++) {
      textFields[i].setText(args[i]);
    }

    // If all arguments are given, attempt to connect to the server
    if (Arrays.stream(args).noneMatch(Objects::isNull)) {
      button.fire();
    }
  }

  /**
   * Displays an error message on screen which fades out after 5 seconds.
   */
  public void displayErrorMessage() {
    this.errorTransition.play();
  }
}
