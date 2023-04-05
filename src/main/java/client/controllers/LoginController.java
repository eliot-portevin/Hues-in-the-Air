package client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class LoginController {

  @FXML
  private Button button;
  @FXML
  private Label labelIp;
  @FXML
  private Label labelPort;
  @FXML
  private TextField textIp;
  @FXML
  private TextField textPort;

  @FXML
  public void initialize() {
    Font bebasFont = Font.loadFont(getClass().getResourceAsStream("/layout/fonts.css"), 10);

    labelIp.setFont(Font.font("Arial", 50));
  }
}
