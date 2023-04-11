package client.controllers;

import client.Client;
import java.util.Arrays;

import client.util.AlertManager;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class LoginController {

  // Grid panes
  @FXML private GridPane titlePane;
  @FXML private HBox hboxLogin;

  // Labels
  @FXML private Label labelUsername;
  @FXML private Label labelIp;
  @FXML private Label labelPort;

  // Title
  @FXML private Label titleHues;
  @FXML private Label titleThe;
  @FXML private Label titleIn;
  @FXML private Label titleAir;

  // Connect to server button
  @FXML private Button button;

  // Text fields
  @FXML private TextField textUsername;
  @FXML private TextField textIp;
  @FXML private TextField textPort;

  // Error message
  @FXML private HBox alertPane;
  @FXML private Label alert;
  public AlertManager alertManager;

  /**
   * Method that is called when the login screen is loaded. The default behaviours are set for the
   * login screen, which includes the tab behaviour, button behaviour and font behaviour.
   */
  @FXML
  public void initialize() {
    // Change text field when <Tab> or <Enter> is clicked
    this.setTabBehaviour();

    // Set behaviour for the login button
    this.setButtonBehaviour();

    // Automatically change font sizes with window size
    this.setFontBehaviour();

    this.alertManager = new AlertManager(alertPane, alert);

    this.textUsername.setPromptText(System.getProperty("user.name"));
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
    if (Arrays.stream(args).noneMatch(arg -> arg.equals(""))) {
      button.fire();
    }
  }
}
