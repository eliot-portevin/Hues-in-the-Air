package client.util;

import client.Client;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;

import static client.Client.bebasItalics;

/**
 * Manages the chat. Contains a TextFlow and a TextField. The TextField is used to type messages, and
 * the TextFlow is used to display messages.
 */
public class Chat {
  private final String chatType;

  private final TextField chatText;
  private final TextFlow chat;
  private final ScrollPane scrollPane;

  /**
   * Whether the chat is in front of the other elements. If true, the chat will be displayed in front.
   */
  public Boolean isInFront = false;

  private final int fontSize = 20;

  /**
   * Constructor for Chat
   *
   * @param chatType The type of chat, either "lobby" or "server"
   * @param chatText The TextField where the user types the message
   * @param chat The TextFlow where the messages are displayed
   * @param scrollPane The ScrollPane where the TextFlow is displayed
   */
  public Chat(String chatType, TextField chatText, TextFlow chat, ScrollPane scrollPane) {
    switch (chatType) {
      case "lobby" -> this.chatType = "lobby";
      case "server" -> this.chatType = "server";
      default -> throw new IllegalStateException("Unexpected value: " + chatType);
    }
    this.chatText = chatText;
    this.chat = chat;
    this.scrollPane = scrollPane;

    this.initialiseChat();
    this.setFontBehaviour();
  }

  /** Initialises the chat. Sets the font and the behaviour of the chat. */
  public void initialiseChat() {
    chatText.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("ENTER")) {
            String message = chatText.getText().trim();

            if (!message.isBlank()) {
              if (message.startsWith("@")) {
                Client.getInstance().sendPrivateMessage(message);
              } else {
                if (chatType.equals("lobby")) {
                  Client.getInstance().sendLobbyMessage(message);
                } else if (chatType.equals("server")) {
                  Client.getInstance().sendPublicMessage(message);
                }
              }
            }
            chatText.clear();
          } else if (e.getCode().toString().equals("ESCAPE")) {
            chatText.getParent().requestFocus();
          }
        });

    // Adding the welcome message
    Text text1 = new Text("Welcome to the chat!\n");
    Text text2 = new Text("Type your message and press enter to send it.\n");
    Text text3 = new Text("Start your message with @username to send a private message.\n\n");
    for (Text t : Arrays.asList(text1, text2, text3)) {
      t.styleProperty().set("-fx-fill: #363636");
      t.setFont(bebasItalics);
    }
    chat.getChildren().addAll(text1, text2, text3);
  }

  /**
   * Sets the position of the chat. If the chat is in front, it is visible. If it is behind, it is
   * invisible.
   *
   * @param isInFront Whether the chat is in front or behind
   */
  public void inFront(Boolean isInFront) {
    this.isInFront = isInFront;
    if (this.isInFront) {
      this.scrollPane.toFront();
      this.chatText.toFront();
    } else {
      this.scrollPane.toBack();
      this.chatText.toBack();
    }
  }

  /**
   * Binds the font size of the chat to the width of the scroll pane. This means that the font size
   * will always be 1/20th of the width of the scroll pane.
   */
  private void setFontBehaviour() {
    this.chatText
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", scrollPane.widthProperty().divide(fontSize), ";"));
    this.chat
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              for (Node node : this.chat.getChildren()) {
                Text text = (Text) node;
                text.setFont(new Font(text.getFont().getName(), newValue.doubleValue() / fontSize));
              }
            });
  }

  /**
   * Appends a received message to the chat in the correct format. Called from the concerned
   * controller when a message is received.
   * @param message The message to be added
   * @param sender The sender of the message
   * @param isPrivate Whether the message is private or not
   */
  public void addMessage(String message, String sender, Boolean isPrivate) {
    Text text =
        new Text(String.format("[%s] %s- %s%n", sender, isPrivate ? "@Private " : "", message));

    text.setFont(
        new Font(
            isPrivate ? "BebasNeuePro-BoldItalic" : "Bebas Neue Regular",
            scrollPane.getWidth() / fontSize));

    this.chat.getChildren().add(text);

    // Scroll to bottom
    this.scrollPane.setVvalue(1.0);
  }

  /**
   * Fills the chat text field with a message and focuses on it. E.g. when a user clicks on a
   * username in the lobby, the chat text field will be filled with the username.
   * @param message The message to be filled in the chat text field
   */
  public void fillTextField(String message) {
    this.chatText.setText(message);
    Platform.runLater(
        () -> {
          this.chatText.requestFocus();
          this.chatText.end();
        });
  }
}
