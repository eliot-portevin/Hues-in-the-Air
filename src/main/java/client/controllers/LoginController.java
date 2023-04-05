package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class LoginController {

  @FXML private GridPane backgroundPane;

  @FXML private Button button;

  @FXML private HBox hboxLogin;

  @FXML private HBox hboxPort;

  @FXML private Label labelIp;

  @FXML private Label labelPort;

  @FXML private TextField textIp;

  @FXML private TextField textPort;

  @FXML private Label titleAir;

  @FXML private Label titleHues;

  @FXML private Label titleIn;

  @FXML private Label titleThe;

  private final Font ralewayFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Raleway.ttf"), 130);

  @FXML
  public void initialize() {
    this.titleHues.setFont(ralewayFont);
  }
}
