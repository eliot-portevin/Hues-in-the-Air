package client.controllers;

import client.Client;
import java.util.Arrays;
import java.util.Objects;

import client.util.AlertManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MenuController {
  // Sub-controllers
  @FXML private MenuHomeController homeTabController;
  @FXML private MenuGameController gamesTabController;
  public MenuSettingsController settingsTabController;

  // Tab windows
  @FXML private VBox homeTab;
  @FXML private VBox gamesTab;
  @FXML private VBox settingsTab;

  @FXML private GridPane backgroundPane;

  // Tab buttons
  @FXML private ToggleButton tabGamesButton;
  @FXML private ToggleButton tabHomeButton;
  @FXML private ToggleButton tabSettingsButton;

  // Right pane
  @FXML private Label title;
  @FXML private ScrollPane scrollPane;
  @FXML private TextFlow chat;
  @FXML private TextField textChat;

  @FXML private HBox alertPane;
  @FXML private Label alert;
  public AlertManager alertManager;

  private final Font bebasItalics =
      Font.loadFont(
          Objects.requireNonNull(getClass().getResource("/fonts/Bebas_Neue_Italics.otf"))
              .toExternalForm(),
          20);
  private final Font bebasRegular =
      Font.loadFont(
          Objects.requireNonNull(getClass().getResource("/fonts/Bebas_Neue_Regular.ttf"))
              .toExternalForm(),
          20);

  public static MenuController instance;

  @FXML
  public void initialize() {
    this.setButtonBehaviour();

    this.setFontBehaviour();

    this.setTabPaneBehaviour();

    this.initialiseChat();

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
    this.textChat.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("ENTER")) {
            String message = this.textChat.getText();

            if (message.startsWith("@")) {
              String recipient = message.split(" ")[0].substring(1);
              String messageContent = message.substring(recipient.length() + 2);
              Client.getInstance().sendMessageClient(recipient, messageContent);
            }
            else {
              Client.getInstance().sendMessageServer(this.textChat.getText());
            }
            this.textChat.clear();
          }
        });
    Text text1 = new Text("Welcome to the chat!\n");
    Text text2 = new Text("Type your message and press enter to send it.\n");
    Text text3 = new Text("Start your message with @username to send a private message.\n\n");
    for (Text t : Arrays.asList(text1, text2, text3)) {
      t.styleProperty().set("-fx-fill: #363636");
      t.setFont(bebasItalics);
    }

    this.chat
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              for (Node node : this.chat.getChildren()) {
                Text text = (Text) node;
                text.setFont(new Font(text.getFont().getName(), newValue.doubleValue() / 25));
              }
            });
    this.chat.getChildren().addAll(text1, text2, text3);
  }

  /** Configures the tabs to play a click sound when the mouse enter them */
  private void setButtonBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHomeButton, tabGamesButton, tabSettingsButton)) {
      tab.setOnMouseEntered(
          e -> {
            if (!tab.isSelected()) {
              Client.getInstance().clickSound();
            }
          });
      tab.setOnAction(e -> System.out.println("Tab clicked"));
    }
  }

  /** Binds the font size of the labels to the width of the window. */
  private void setFontBehaviour() {
    tabHomeButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabGamesButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    tabSettingsButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    title
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(24)));
    textChat
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", homeTab.widthProperty().divide(30)));
  }

  /**
   * Selects the home tab for the startup. Also sets the behaviour for the tabs, so that only one
   * tab can be selected at a time.
   */
  private void setTabPaneBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHomeButton, tabGamesButton, tabSettingsButton)) {
      tab.setOnAction(
          e -> {
            for (ToggleButton otherTab :
                Arrays.asList(tabHomeButton, tabGamesButton, tabSettingsButton)) {
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
      } else if (tab == tabGamesButton) {
        gamesTab.toFront();
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
    Text text =
        new Text(
            String.format(
                "[%s] %s- %s%n",
                sender, Objects.equals(privacy, "Private") ? "@Private " : "", message));

    text.setFont(
        new Font(
            privacy.equals("Private") ? "BebasNeuePro-BoldItalic" : "Bebas Neue Regular",
            this.chat.getWidth() / 25));

    this.chat.getChildren().add(text);
    this.scrollPane.setVvalue(1.0);
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
    this.textChat.setText(text);
    this.textChat.requestFocus();
    this.textChat.end();
  }
}
