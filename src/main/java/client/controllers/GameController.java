package client.controllers;

import client.Client;
import client.Game;
import client.util.AlertManager;
import client.util.Chat;
import javafx.application.Platform;
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
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import server.ServerProtocol;

import java.util.Arrays;

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

  private Client client;
  /** Initializes the controller class. */
  public void initialize() {

    this.initialiseKeyboard();

    this.initialiseChats();
    this.setChatTabsBehaviour();

    this.alertManager = new AlertManager(alertPane, alert);
  }

  /** Starts the game. Is required because otherwise the client is being set too late otherwise */
  public void startGame() {
    this.game = new Game(this.client);
    game.run(this.gamePane);
  }
  /** getter for the game */
  public Game getGame() {
    return this.game;
  }
  /** setter for the client */
  public void setClient(Client client) {
    this.client = client;
  }

  /** Sets the behaviour for detected key presses and pause the game if escape is pressed */
  private void initialiseKeyboard() {
    backgroundPane.setOnKeyPressed(
        e -> {
          if (!(lobbyChatText.isFocused() || serverChatText.isFocused())) {
            game.keys.put(e.getCode(), true);
          }
        });
    backgroundPane.setOnKeyReleased(
        e -> {
          if (!(lobbyChatText.isFocused() || serverChatText.isFocused())) {
            game.keys.put(e.getCode(), false);

            if (e.getCode() == KeyCode.SPACE) {
              game.jumpRequestSent = false;
            }
          }
        });

    Platform.runLater(() -> backgroundPane.requestFocus());
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

  /** Sets the font size of the tab button based on whether it is selected or not. */
  private void setTabFontSize(ToggleButton tabButton) {
    tabButton
        .styleProperty()
        .bind(
            Bindings.concat(
                "-fx-font-size: ",
                lobbyChatPane.widthProperty().divide(tabButton.isSelected() ? 15 : 18)));
  }

  /** The client has received a message. The message is displayed in the corresponding chat pane. */
  public void receiveMessage(String message, String sender, String privacy) {
    switch (privacy) {
      case "Lobby" -> this.lobbyChatManager.addMessage(message, sender, false);
      case "Public" -> this.serverChatManager.addMessage(message, sender, false);
      case "Private" -> {
        if (this.lobbyChatManager.isInFront) {
          this.lobbyChatManager.addMessage(message, sender, true);
        } else if (this.serverChatManager.isInFront) {
          this.serverChatManager.addMessage(message, sender, true);
        }
      }
      default -> throw new IllegalStateException("Unexpected value: " + privacy);
    }
  }

  /**
   * The server has sent the list of critical blocks required to colour the whole level. The whole level
   * is coloured accordingly.
   * @param command The command sent by the server
   */
  public void setBlockColours(String command) {
    String[] blocks = command.split(ServerProtocol.SUBSEPARATOR.toString());

    for (String block : blocks) {
      if (block.equals("")) {
        continue;
      }
      String[] blockInfo = block.split(ServerProtocol.SUBSUBSEPARATOR.toString());
      int x = Integer.parseInt(blockInfo[0]);
      int y = Integer.parseInt(blockInfo[1]);
      Color colour = Color.valueOf(blockInfo[2]);

      this.game.setBlockColour(x, y, colour);
    }
  }

  /** The server has sent the list of critical blocks required to colour the whole level. The whole level
   * is coloured accordingly.
   * @param path The path to the level data file
   */
  public void loadLevel(String path){
    // TODO: loadclient level
    System.out.println("Loading level: " + path);
  }

}


