package client.controllers;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.Arrays;

public class MenuController {

  @FXML private GridPane backgroundPane;

  @FXML private Button buttonCreateLobby;

  @FXML private Button buttonJoinLobby;

  @FXML private HBox lobbyConnectionPane;

  @FXML private ToggleButton tabGames;

  @FXML private ToggleButton tabHome;

  @FXML private ToggleButton tabSettings;

  @FXML private TextField textLobbyName;

  @FXML private PasswordField textLobbyPassword;

  @FXML
  public void initialize() {
    // Change text field when <Tab> or <Enter> is clicked
    this.setTabBehaviour();

    // Set behaviour for the login button
    this.setButtonBehaviour();

    // Automatically change font sizes with window size
    this.setFontBehaviour();

    // Set the behaviour for the tabpane
    this.setTabpaneBehaviour();
  }

  private void setTabBehaviour() {}

  private void setButtonBehaviour() {}

  /** Binds the font size of the labels to the width of the window. */
  private void setFontBehaviour() {
    textLobbyName
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(60)));
    textLobbyPassword
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(60)));

    buttonCreateLobby
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    buttonJoinLobby
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));

    tabHome
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabGames
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabSettings
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
  }

  /**
   * Selects the home tab for the startup. Also sets the behaviour for the tabs, so that only one
   * tab can be selected at a time.
   */
  private void setTabpaneBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHome, tabGames, tabSettings)) {
      tab.setOnAction(e -> {
        for (ToggleButton otherTab : Arrays.asList(tabHome, tabGames, tabSettings)) {
          if (otherTab != tab) {
            configureTab(otherTab, false);
          }
        }
        configureTab(tab, true);
      });
    }
    configureTab(tabHome, true);
  }

  /**
   * Configures a tab, so that it is selected or not selected. Also sets the font size of the tab
   * depending on whether it is selected or not.
   * @param tab The ToggleButton to configure
   * @param isSelected Whether the tab should be selected or not
   */
  private void configureTab(ToggleButton tab, boolean isSelected) {
    int fontSize = isSelected ? 45 : 50;
    tab.styleProperty().bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(fontSize)));
    tab.setSelected(isSelected);
  }
}
