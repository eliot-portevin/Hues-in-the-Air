package client.controllers;

import client.Game;
import client.util.AlertManager;
import client.util.Chat;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;

public class GameController {
  @FXML private GridPane backgroundPane;
  @FXML private Pane gamePane;

  @FXML private ScrollPane lobbyChatPane;
  @FXML private ScrollPane serverChatPane;
  @FXML private ToggleButton lobbyTabButton;
  @FXML private ToggleButton serverTabButton;

  @FXML private TextFlow lobbyChat;
  @FXML private TextFlow serverChat;
  @FXML private TextField lobbyChatText;
  @FXML private TextField serverChatText;

  private Chat lobbyChatManager;
  private Chat serverChatManager;

  @FXML private HBox alertPane;
  @FXML private Label alert;
  public AlertManager alertManager;

  public Game game;

  public void initialize() {
    this.game = new Game();
    game.run(this.gamePane);

    this.initialiseKeyboard();

    this.initialiseChats();
    this.setChatTabsBehaviour();

    this.alertManager = new AlertManager(alertPane, alert);
  }

  public Game getGame() {
    return this.game;
  }

  /**
   * Sets the behaviour for detected key presses
   * and pause the game if escape is pressed
   */
  private void initialiseKeyboard() {
    backgroundPane.setOnKeyPressed(e -> {
      if (!(lobbyChatText.isFocused() || serverChatText.isFocused())) {
        if (e.getCode() != KeyCode.ESCAPE) {
          game.keys.put(e.getCode(), true);
        }
      }
    });
    backgroundPane.setOnKeyReleased(e -> {
      if (!(lobbyChatText.isFocused() || serverChatText.isFocused())) {
        if (e.getCode() == KeyCode.ESCAPE) {
          game.pause = !game.pause;
        }
        game.keys.put(e.getCode(), false);
      }
    });
    backgroundPane.requestFocus();
  }

  /** Creates the chat objects for the right pane. */
  private void initialiseChats() {
    this.lobbyChatManager = new Chat("lobby", lobbyChatText, lobbyChat, lobbyChatPane);
    this.serverChatManager = new Chat("server", serverChatText, serverChat, serverChatPane);
    this.lobbyChatManager.inFront(true);
  }

  /**
   * Sets the behaviour for the chat tabs. This includes what pane should be shown and resetting the
   * button font.
   */
  private void setChatTabsBehaviour() {
    this.lobbyTabButton.setOnAction(
        e -> {
          this.serverTabButton.setSelected(false);
          this.lobbyChatManager.inFront(true);
          this.serverChatManager.inFront(false);

          setTabFontSize(lobbyTabButton);
          setTabFontSize(serverTabButton);
        });

    this.serverTabButton.setOnAction(
        e -> {
          this.lobbyChatManager.inFront(false);
          this.serverChatManager.inFront(true);
          this.lobbyTabButton.setSelected(false);

          setTabFontSize(lobbyTabButton);
          setTabFontSize(serverTabButton);
        });

    // Setting the default tab
    this.lobbyTabButton.fire();
  }

  /**
   * Sets the font size of the tab button based on whether it is selected or not.
   */
  private void setTabFontSize(ToggleButton tabButton) {
    tabButton
        .styleProperty()
        .bind(
            Bindings.concat(
                "-fx-font-size: ",
                lobbyChatPane.widthProperty().divide(tabButton.isSelected() ? 15 : 18)));
  }

  /**
   * The client has received a message. The message is displayed in the corresponding chat pane.
   */
  public void receiveMessage(String message, String sender, String privacy) {
    switch (privacy) {
      case "Lobby" -> this.lobbyChatManager.addMessage(message, sender, false);
      case "Public" -> this.serverChatManager.addMessage(message, sender, false);
      case "Private" -> {
        if (this.lobbyChatManager.isInFront) {
          this.lobbyChatManager.addMessage(message, sender, true);
        } else if (this.serverChatManager.isInFront){
          this.serverChatManager.addMessage(message, sender, true);
        }
      }
      default -> throw new IllegalStateException("Unexpected value: " + privacy);
    }
  }
}
