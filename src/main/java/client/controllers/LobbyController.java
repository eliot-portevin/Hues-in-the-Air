package client.controllers;

import client.Client;
import client.util.AlertManager;
import client.util.Chat;
import java.util.ArrayList;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class LobbyController {
  // Chat
  @FXML private GridPane backgroundPane;
  @FXML private ScrollPane lobbyChatPane;
  @FXML private ScrollPane serverChatPane;
  @FXML private TextFlow lobbyChat;
  @FXML private TextFlow serverChat;
  @FXML private TextField lobbyChatText;
  @FXML private TextField serverChatText;
  private Chat lobbyChatManager;
  private Chat serverChatManager;

  @FXML private ToggleButton lobbyTabButton;
  @FXML private ToggleButton serverTabButton;

  @FXML private Button logoutButton;
  @FXML private Label lobbyNameLabel;

  @FXML private Label membersLabel;
  @FXML private ListView<HBox> nameList;
  @FXML private ListView<String> readyList;
  @FXML private ToggleButton toggleReadyButton;

  @FXML private HBox alertPane;
  @FXML private Label alert;
  public AlertManager alertManager;

  public void initialize() {
    this.initialiseChats();
    this.setChatTabsBehaviour();

    this.setButtonBehaviour();

    this.setFontBehaviour();

    this.initialiseLobbyList();

    this.alertManager = new AlertManager(alertPane, alert);

    this.lobbyNameLabel.setText(Client.getInstance().getLobbyName());
  }

  /**
   * Initialises the LobbyList format: player's status is aligned right and the colour's height has
   * to be bound to the height of the other cells
   */
  private void initialiseLobbyList() {
    nameList.setCellFactory(
        l -> {
          ListCell<HBox> cell =
              new ListCell<>() {
                @Override
                protected void updateItem(HBox item, boolean empty) {
                  super.updateItem(item, empty);
                  if (empty || item == null || item.getChildren().size() != 2) {
                    setText(null);
                  } else {
                    setAccessibleText(item.getAccessibleText());
                    setAlignment(Pos.CENTER_LEFT);
                    item.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(item);
                  }
                }
              };
          cell.setOnMouseClicked(
              e -> {
                if (!cell.isEmpty()) {
                  Chat chat = serverChatManager.isInFront ? serverChatManager : lobbyChatManager;
                  chat.fillTextField("@" + (cell.getAccessibleText() + " "));
                }
              });
          return cell;
        });
    readyList.setCellFactory(
        l ->
            new ListCell<>() {
              @Override
              protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                  setText(null);
                } else {
                  HBox hBox = new HBox();
                  Text text = new Text(item);
                  hBox.setAlignment(Pos.CENTER_RIGHT);
                  text.setTextAlignment(TextAlignment.RIGHT);
                  hBox.getChildren().add(text);

                  setAlignment(Pos.CENTER_RIGHT);
                  setGraphic(hBox);
                }
              }
            });
  }

  /** Binds the font size of the labels to the size of the window. */
  private void setFontBehaviour() {
    this.lobbyNameLabel
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(15)));
    this.membersLabel
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(30)));
    this.toggleReadyButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    this.logoutButton
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    this.nameList
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    this.readyList
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
  }

  /**
   * Sets the behaviour for the buttons on the lobby screen. This includes the "log out" and "toggle
   * ready" button.
   */
  private void setButtonBehaviour() {
    logoutButton.setOnAction(e -> Client.getInstance().exitLobby());
    toggleReadyButton.setOnAction(
        e -> Client.getInstance().sendToggleReady(toggleReadyButton.isSelected()));

    logoutButton.setOnMouseEntered(e -> Client.getInstance().clickSound());
    toggleReadyButton.setOnMouseEntered(e -> Client.getInstance().clickSound());
    lobbyTabButton.setOnMouseEntered(
        e -> {
          if (!lobbyTabButton.isSelected()) Client.getInstance().clickSound();
        });
    serverTabButton.setOnMouseEntered(
        e -> {
          if (!serverTabButton.isSelected()) Client.getInstance().clickSound();
        });
  }

  /**
   * The client has received confirmation of their toggle. The toggle button is updated accordingly.
   *
   * @param isReady Whether the client is ready or not.
   */
  public void setToggleReady(String isReady) {
    toggleReadyButton.setSelected(isReady.equals("true"));
    toggleReadyButton.setText(isReady.equals("true") ? "Cancel" : "Toggle Ready");
  }

  /**
   * Sets the behaviour for the tab buttons on top of the chat. When a button is clicked, the
   * corresponding chat pane is brought to the front.
   */
  private void setChatTabsBehaviour() {
    lobbyTabButton.setOnAction(
        event -> {
          lobbyTabButton.setSelected(true);
          serverTabButton.setSelected(false);
          lobbyChatManager.inFront(true);
          serverChatManager.inFront(false);
          lobbyTabButton
              .styleProperty()
              .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(45)));
          serverTabButton
              .styleProperty()
              .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
        });

    serverTabButton.setOnAction(
        event -> {
          serverTabButton.setSelected(true);
          lobbyTabButton.setSelected(false);
          serverChatManager.inFront(true);
          lobbyChatManager.inFront(false);
          lobbyTabButton
              .styleProperty()
              .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
          serverTabButton
              .styleProperty()
              .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(45)));
        });

    lobbyTabButton.setSelected(false);
    lobbyTabButton.fire();
  }

  /** Initialises the chat with a welcome message and sets the size according to the window */
  private void initialiseChats() {
    this.lobbyChatManager = new Chat("lobby", lobbyChatText, lobbyChat, lobbyChatPane);
    this.serverChatManager = new Chat("server", serverChatText, serverChat, serverChatPane);
    lobbyChatManager.inFront(true);
  }

  /**
   * Receives a message from the server and displays it in the corresponding chat pane.
   *
   * @param message The message to be displayed.
   * @param sender The sender of the message.
   * @param privacy Whether the message is "Private", "Lobby" or "Server".
   */
  public void receiveMessage(String message, String sender, String privacy) {
    switch (privacy) {
      case "Public" -> this.serverChatManager.addMessage(message, sender, false);
      case "Lobby" -> this.lobbyChatManager.addMessage(message, sender, false);
      case "Private" -> {
        if (this.serverChatManager.isInFront) {
          this.serverChatManager.addMessage(message, sender, true);
        } else {
          this.lobbyChatManager.addMessage(message, sender, true);
        }
      }
    }
  }

  /**
   * Updates the lobby list with the new list of clients in the lobby and their ready status.
   *
   * @param clients The list of clients in the lobby and their status separated by a space.
   */
  public void updateLobbyList(String[] clients) {
    ArrayList<HBox> clientNames = new ArrayList<>();
    ArrayList<String> clientReady = new ArrayList<>();

    for (String client : clients) {
      String[] clientInfo = client.split(" ");

      // Name
      HBox clientNameAndColour = new HBox();
      Text colour = new Text("■ ");
      // ■
      Text name =
          new Text(
              clientInfo[0].equals(Client.getInstance().getUsername()) ? "You" : clientInfo[0]);
      colour.setFill(Color.valueOf(clientInfo[2]));
      clientNameAndColour.getChildren().addAll(colour, name);
      clientNameAndColour.setAccessibleText(name.getText());
      clientNames.add(clientNameAndColour);

      // Status
      clientReady.add(clientInfo[1].equals("true") ? "Ready" : "...");
    }

    this.nameList.getItems().setAll(clientNames);
    this.readyList.getItems().setAll(clientReady);
  }
}
