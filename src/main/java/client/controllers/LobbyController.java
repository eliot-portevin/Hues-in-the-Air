package client.controllers;

import client.Client;
import client.util.AlertManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LobbyController {
  // Chat
  @FXML private GridPane backgroundPane;
  @FXML private ScrollPane lobbyChatPane;
  @FXML private ScrollPane serverChatPane;
  @FXML private TextFlow lobbyChat;
  @FXML private TextFlow serverChat;
  @FXML private TextField chatText;

  @FXML private ToggleButton lobbyTabButton;
  @FXML private ToggleButton serverTabButton;

  @FXML private Button logoutButton;
  @FXML private Label lobbyNameLabel;

  @FXML private GridPane rightPane;
  @FXML private Label membersLabel;
  @FXML private ListView<String> nameList;
  @FXML private ListView<String> readyList;
  @FXML private ToggleButton toggleReadyButton;

  private Boolean lobbyChatInFront = true;

  @FXML private HBox alertPane;
  @FXML private Label alert;
  private AlertManager alertManager;

  public void initialize() {
    this.setButtonBehaviour();

    this.setFontBehaviour();

    this.setTabPaneBehaviour();

    this.initialiseChats();

    this.initialiseLobbyList();

    this.alertManager = new AlertManager(alertPane, alert);

    this.lobbyNameLabel.setText(Client.getInstance().getLobbyName());
  }

  private void initialiseLobbyList() {
    for (ListView<String> list : Arrays.asList(nameList, readyList)) {
      list.setCellFactory(
          param ->
              new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                  super.updateItem(item, empty);
                  if (empty || item == null) {
                    return;
                  } else {
                    // set the width's
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());

                    // allow wrapping
                    setWrapText(true);

                    setText(item.toString());
                  }
                }
              });
    }
  }

  /** Binds the font size of the labels to the size of the window. */
  private void setFontBehaviour() {
    this.lobbyNameLabel
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(15)));
    this.chatText
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", lobbyChat.widthProperty().divide(25)));
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
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(60)));
    this.readyList
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(60)));
  }

  /**
   * Sets the behaviour for the buttons on the lobby screen. This includes the "log out" and "toggle
   * ready" button.
   */
  private void setButtonBehaviour() {
    logoutButton.setOnAction(e -> Client.getInstance().exitLobby());
    toggleReadyButton.setOnAction(
        e -> Client.getInstance().sendToggleReady(toggleReadyButton.isSelected()));
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
  private void setTabPaneBehaviour() {
    lobbyTabButton.setOnAction(
        event -> {
          lobbyTabButton.setSelected(true);
          serverTabButton.setSelected(false);
          lobbyChatPane.toFront();
          lobbyChatInFront = true;
        });

    serverTabButton.setOnAction(
        event -> {
          serverTabButton.setSelected(true);
          lobbyTabButton.setSelected(false);
          serverChatPane.toFront();
          lobbyChatInFront = false;
        });

    lobbyTabButton.setSelected(lobbyChatInFront);
  }

  private void initialiseChats() {
    this.chatText.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("ENTER")) {
            String message = chatText.getText();

            if (message.startsWith("@")) {
              String recipient = message.split(" ")[0].substring(1);
              String messageContent = message.substring(recipient.length() + 2);
              Client.getInstance().sendMessageClient(recipient, messageContent);
            } else {
              if (lobbyChatInFront) {
                Client.getInstance().sendMessageLobby(message);
              } else {
                Client.getInstance().sendMessageServer(message);
              }
            }
            this.chatText.clear();
          }
        });
    this.lobbyChatPane.toFront();
  }

  /**
   * Receives a message from the server and displays it in the corresponding chat pane.
   *
   * @param message The message to be displayed.
   * @param sender The sender of the message.
   * @param privacy Whether the message is "Private", "Lobby" or "Server".
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
            this.lobbyChatPane.getWidth() / 25));

    switch (privacy) {
      case "Private", "Public" -> {
        this.serverChat.getChildren().add(text);
        this.serverChatPane.setVvalue(1.0);
      }
      case "Lobby" -> {
        this.lobbyChat.getChildren().add(text);
        this.lobbyChatPane.setVvalue(1.0);
      }
    }
  }

  /**
   * Updates the lobby list with the new list of clients in the lobby and their ready status.
   *
   * @param clients The list of clients in the lobby and their status separated by a space.
   */
  public void updateLobbyList(String[] clients) {
    this.nameList.getItems().clear();
    this.readyList.getItems().clear();

    ArrayList<String> clientNames = new ArrayList<>();
    ArrayList<String> clientReady = new ArrayList<>();

    for (String client : clients) {
      String[] clientInfo = client.split(" ");
      clientNames.add(clientInfo[0]);
      clientReady.add(clientInfo[1]);
    }

    this.nameList.getItems().setAll(clientNames);
    this.readyList.getItems().setAll(clientReady);
  }
}
