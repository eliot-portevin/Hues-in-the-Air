package client.controllers;

import client.Client;
import client.ClientGame;
import client.ClientProtocol;
import client.util.AlertManager;
import client.util.Chat;
//import com.studiohartman.jamepad.ControllerIndex;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
// import com.studiohartman.jamepad.ControllerManager;
// import com.studiohartman.jamepad.ControllerState;

/** The controller for the game window. */
public class GameController {
  /** The background of the Pane. */
  @FXML private GridPane backgroundPane;
  /** The Pane of the game. */
  @FXML private Pane gamePane;
  /** The pane of the chat of the lobby. */
  @FXML private ScrollPane lobbyChatPane;
  /** The pane of the chat of the server. */
  @FXML private ScrollPane serverChatPane;
  /** The ToggleButton for the lobby. */
  @FXML private ToggleButton lobbyTabButton;
  /** The ToggleButton for the server. */
  @FXML private ToggleButton serverTabButton;

  /** The TextFlow for the lobbyChat. */
  @FXML private TextFlow lobbyChat;
  /** The TextFlow for the serverChat. */
  @FXML private TextFlow serverChat;
  /** The TextField for the lobbyChat. */
  @FXML private TextField lobbyChatText;
  /** The TextField for the serverChat. */
  @FXML private TextField serverChatText;

  private Chat lobbyChatManager;
  private Chat serverChatManager;
  /** The HBox, used when the pane is resized. */
  @FXML private HBox alertPane;
  /** The Label to alert. */
  @FXML private Label alert;

  /** The Button to quit the game. */
  @FXML Button quitButton;
  /** The Label of how many lives the player has left. */
  @FXML Label livesLabel;
  /** The Label of how many levels the player has accomplished. */
  @FXML Label scoreLabel;

  /** The game. */
  private ClientGame game;
  /** The theClient of the game. */
  private Client client;

  /** Initializes the controller class. */
  public void initialize() {

    this.initialiseKeyboard();
    this.initialiseChats();
    this.setChatTabsBehaviour();
    this.setFontBehaviour();
    this.setButtonBehaviour();

    AlertManager alertManager = new AlertManager(alertPane, alert);
  }
  /** Starts the game.
   *Is required because otherwise the theClient is being set too late. */
  public void startGame() {
    this.game = new ClientGame(this.client, this.gamePane);
    game.run();
  }
  /** Getter for the game.
   *
   * @return the game
   */
  public ClientGame getGame() {
    return this.game;
  }
  /**
   * Setter for the theClient.
   *
   * @param theClient the theClient connected to this controller
   */
  public void setClient(final Client theClient) {
    this.client = theClient;
  }

  /** Sets the behaviour for detected key presses
   * and pause the game if escape is pressed. */
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

  /** Bind the font sizes to the size of the window. */
  private void setFontBehaviour() {
    this.quitButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ",
                backgroundPane.widthProperty().divide(50)));
    this.livesLabel
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ",
                backgroundPane.widthProperty().divide(60)));
    this.scoreLabel
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ",
                backgroundPane.widthProperty().divide(60)));
  }

  /**
   * Sets the behaviour for the "Quit game" button.
   * This includes sending a message to the server
   * requesting that the game be ended.
   */
  private void setButtonBehaviour() {
    this.quitButton.setOnAction(
        e -> this.client.sendGameCommand(ClientProtocol.
                REQUEST_END_GAME.toString()));
  }

  /** Creates the chat objects for the right pane. */
  private void initialiseChats() {
    this.lobbyChatManager = new Chat("lobby",
            lobbyChatText, lobbyChat, lobbyChatPane);
    this.serverChatManager = new Chat("server",
            serverChatText, serverChat, serverChatPane);
    this.lobbyChatManager.inFront(true);
  }

  /**
   * Sets the behaviour for the chat tabs.
   * This includes what pane should be shown
   * and resetting the button font.
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
   * Sets the font size of the tab button
   * based on whether it is selected or not.
   *
   * @param tabButton the button to set the font size of
   */
  private void setTabFontSize(final ToggleButton tabButton) {
    tabButton
        .styleProperty()
        .bind(
            Bindings.concat(
                "-fx-font-size: ",
                lobbyChatPane.widthProperty().divide(tabButton.
                        isSelected() ? 15 : 18)));
  }

  /**
   * The theClient has received a message.
   * The message is displayed in the corresponding chat pane.
   *
   * @param message the message to display
   * @param sender the sender of the message
   * @param privacy the privacy of the message
   */
  public void receiveMessage(final String message, final String sender,
                             final String privacy) {
    switch (privacy) {
      case "Lobby" -> this.lobbyChatManager.addMessage(message, sender, false);
      case "Public" -> this.serverChatManager.
              addMessage(message, sender, false);
      case "Private" -> {
        if (this.lobbyChatManager.isInFront) {
          this.lobbyChatManager.addMessage(message, sender, true);
        } else if (this.serverChatManager.isInFront) {
          this.serverChatManager.addMessage(message, sender, true);
        }
      }
      default -> throw new IllegalStateException("Unexpected value: "
              + privacy);
    }
  }

  /**
   * The server has sent the list of critical blocks
   * required to colour the whole level.
   * The whole level is coloured accordingly.
   *
   * @param command The command sent by the server
   */
  public void setBlockColours(final String command) {
    String[] blocks = command.split(ServerProtocol.SUBSEPARATOR.toString());

    for (String block : blocks) {
      if (block.equals("")) {
        continue;
      }
      String[] blockInfo = block.split(ServerProtocol.
              SUBSUBSEPARATOR.toString());
      int x = Integer.parseInt(blockInfo[0]);
      int y = Integer.parseInt(blockInfo[1]);
      Color colour = Color.valueOf(blockInfo[2]);

      this.game.setBlockColour(x, y, colour);
    }
  }

  /**
   * The server has sent the list of critical blocks
   * required to colour the whole level.
   * The whole level is coloured accordingly.
   *
   * @param levelPath The levelPath to the level data file
   */
  public void loadLevel(final String levelPath) {
    this.game.loadLevel(levelPath);
  }

  /**
   * The server has sent the number of lives remaining
   * and the number of levels completed.
   * The corresponding labels are updated.
   *
   * @param livesRemaining the number of lives remaining
   * @param levelsCompleted the number of levels completed
   */
  public void updateGameStatus(final String livesRemaining,
                               final String levelsCompleted) {
    this.livesLabel.setText(
        "Lives: "
            + (livesRemaining.equals(String.valueOf(Integer.MAX_VALUE))
                ? "∞" : livesRemaining));
    this.scoreLabel.setText("Levels completed: " + levelsCompleted);
  }
}
