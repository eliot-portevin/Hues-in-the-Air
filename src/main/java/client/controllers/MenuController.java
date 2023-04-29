package client.controllers;

import client.Client;
import client.util.AlertManager;
import client.util.Chat;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/** The controller for the menu window. */
public class MenuController {
  // Sub-controllers
  @FXML private MenuHomeController homeTabController;
  @FXML private MenuGameController highscoreTabController;
  /** The settings controller */
  public MenuSettingsController settingsTabController;

  // Tab windows
  @FXML private VBox homeTab;
  @FXML private VBox highscoreTab;
  @FXML private VBox settingsTab;

  @FXML private GridPane backgroundPane;

  // Tab buttons
  @FXML private ToggleButton tabHighscoresButton;
  @FXML private ToggleButton tabHomeButton;
  @FXML private ToggleButton tabSettingsButton;

  // Right pane
  @FXML private Label title;
  @FXML private ScrollPane scrollPane;
  @FXML private TextFlow chat;
  @FXML private TextField textChat;
  private Chat chatManager;

  @FXML private HBox alertPane;
  @FXML private Label alert;
  /** The alert manager for the menu pane */
  public AlertManager alertManager;

  /** The instance of the MenuController */
  public static MenuController instance;

  /**
   * Initializes the controller class. This method is automatically called after the fxml file has
   * been loaded.
   */
  @FXML
  public void initialize() {
    this.initialiseChat();

    this.setButtonBehaviour();

    this.setFontBehaviour();

    this.setTabButtonsBehaviour();

    instance = this;
    this.alertManager = new AlertManager(alertPane, alert);
  }

  /**
   * Returns the instance of the MenuController. Called from other controllers to access the chat
   *
   * @return the instance of the MenuController
   */
  public static MenuController getInstance() {
    return instance;
  }

  /**
   * If the user presses "Enter" in the chat box, the message is sent to the server and the chat box
   * is cleared.
   */
  private void initialiseChat() {
    this.chatManager = new Chat("server", textChat, chat, scrollPane);
    this.chatManager.inFront(true);
  }

  /** Configures the tabs to play a click sound when the mouse enter them */
  private void setButtonBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHomeButton, tabHighscoresButton, tabSettingsButton)) {
      tab.setOnMouseEntered(
          e -> {
            if (!tab.isSelected()) {
              Client.getInstance().clickSound();
            }
          });
    }
  }

  /** Binds the font size of the labels to the width of the window. */
  private void setFontBehaviour() {
    tabHomeButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabHighscoresButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabSettingsButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    title
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(24)));
  }

  /**
   * Selects the home tab for the startup. Also sets the behaviour for the tabs, so that only one
   * tab can be selected at a time.
   */
  private void setTabButtonsBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHomeButton, tabHighscoresButton, tabSettingsButton)) {
      tab.setOnAction(
          e -> {
            for (ToggleButton otherTab :
                Arrays.asList(tabHomeButton, tabHighscoresButton, tabSettingsButton)) {
              if (otherTab != tab) {
                configureTab(otherTab, false);
              }
            }
            configureTab(tab, true);
          });
    }
    configureTab(tabHomeButton, true);
  }

  /**
   * Configures a tab, so that it is selected or not selected. Also sets the font size of the tab
   * depending on whether it is selected or not.
   *
   * @param tab The ToggleButton to configure
   * @param isSelected Whether the tab should be selected or not
   */
  private void configureTab(ToggleButton tab, boolean isSelected) {
    if (isSelected) {
      if (tab == tabHomeButton) {
        homeTab.toFront();
      } else if (tab == tabHighscoresButton) {
        highscoreTab.toFront();
      } else if (tab == tabSettingsButton) {
        settingsTab.toFront();
      }
    }
    int fontSize = isSelected ? 45 : 50;
    tab.styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(fontSize)));
    tab.setSelected(isSelected);
  }

  /**
   * The client has received a message from the server or from another client. The message is
   * displayed in the chat box.
   *
   * @param message The message to display
   * @param sender The sender of the message
   * @param privacy Whether the message is private or not
   */
  public void receiveMessage(String message, String sender, String privacy) {
    chatManager.addMessage(message, sender, privacy.equals("Private"));
  }

  /**
   * Sets the lobby list in the home tab.
   *
   * @param lobbyInfo All the information about the lobbies
   */
  public void setLobbyList(String[][] lobbyInfo) {
    this.homeTabController.setLobbyList(lobbyInfo);
  }

  /**
   * Sets the game list in the games tab.
   *
   * @param games The list of games and whether they are being played or not
   */
  public void setGameList(String[] games) {
    this.highscoreTabController.setGameList(games);
  }

  /**
   * Updates the list of users in the home tab.
   *
   * @param users A String array containing all the usernames
   */
  public void setUsersList(String[] users) {
    this.homeTabController.setUsersList(users);
  }

  /** Clear the text fields in the home tab. */
  public void clearHomeTab() {
    this.homeTabController.clear();
  }

  /**
   * Sets the text in the chat box. Used to fill with username when the client has clicked on a user
   * in the user list.
   *
   * @param text The text to set
   */
  public void fillChatText(String text) {
    Platform.runLater(
        () -> {
          this.textChat.setText(text);
          this.textChat.requestFocus();
          this.textChat.end();
        });
  }
}
