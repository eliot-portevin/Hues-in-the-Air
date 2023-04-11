package client.controllers;

import client.Client;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class MenuHomeController {
  @FXML private VBox homeTab;

  @FXML private Button buttonCreateLobby;
  @FXML private Button buttonJoinLobby;

  @FXML private TextField textLobbyName;
  @FXML private PasswordField textLobbyPassword;

  @FXML private Label labelLobbies;
  @FXML private TreeView<String> tree;
  private final TreeItem<String> root = new TreeItem<>("");
  private final TreeItem<String> lobbiesHeader = new TreeItem<>("Lobbies (empty)");
  private final TreeItem<String> usersHeader = new TreeItem<>("Users");

  public void initialize() {
    this.setFontBehaviour();

    this.setTabBehaviour();

    this.setButtonBehaviour();

    this.initialiseLobbyList();

    this.textLobbyName.setFocusTraversable(false);
    this.textLobbyPassword.setFocusTraversable(false);
    this.buttonCreateLobby.setFocusTraversable(false);
    this.buttonJoinLobby.setFocusTraversable(false);
  }

  /** Binds the font sizes of various elements to the width of the tab pane. */
  private void setFontBehaviour() {
    this.labelLobbies
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", this.homeTab.widthProperty().divide(20)));
    this.buttonCreateLobby
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", this.homeTab.widthProperty().divide(30)));
    this.buttonJoinLobby
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", this.homeTab.widthProperty().divide(30)));
    this.textLobbyName
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", this.homeTab.widthProperty().divide(30)));
    this.textLobbyPassword
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", this.homeTab.widthProperty().divide(30)));
    tree.styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", homeTab.widthProperty().divide(40)));
  }

  /**
   * When the user types <Tab> or <Enter> in the text field, the focus will be changed to the next
   * field.
   */
  private void setTabBehaviour() {
    this.textLobbyName.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
            this.textLobbyPassword.requestFocus();
          }
        });

    this.textLobbyPassword.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
            this.buttonCreateLobby.requestFocus();
          }
        });

    this.buttonCreateLobby.setOnKeyPressed(
        event -> {
          switch (event.getCode()) {
            case TAB -> this.buttonJoinLobby.requestFocus();
            case ENTER -> this.buttonCreateLobby.fire();
          }
        });

    this.buttonJoinLobby.setOnKeyPressed(
        event -> {
          switch (event.getCode()) {
            case TAB -> this.textLobbyName.requestFocus();
            case ENTER -> this.buttonJoinLobby.fire();
          }
        });
  }

  /** Makes the buttons call the appropriate methods in the Client class when clicked. */
  private void setButtonBehaviour() {
    buttonCreateLobby.setOnAction(
        e -> {
          Client.getInstance().createLobby(textLobbyName.getText(), textLobbyPassword.getText());
          homeTab.requestFocus();
        });
    buttonJoinLobby.setOnAction(
        e -> {
          Client.getInstance().joinLobby(textLobbyName.getText(), textLobbyPassword.getText());
          homeTab.requestFocus();
        });

    buttonCreateLobby.setOnMouseEntered(e -> Client.getInstance().clickSound());
    buttonJoinLobby.setOnMouseEntered(e -> Client.getInstance().clickSound());
  }

  /** Adds the lobbies and users headers to the lobby list. */
  private void initialiseLobbyList() {
    this.tree.setRoot(this.root);
    this.tree.setShowRoot(false);
    this.root.getChildren().add(this.lobbiesHeader);
    this.root.getChildren().add(this.usersHeader);
    this.lobbiesHeader.setExpanded(true);

    this.tree
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue != null) {
                if (newValue.getParent() == this.usersHeader
                    || newValue.getParent().getParent() == this.lobbiesHeader) {
                  MenuController.getInstance().fillChatText("@" + newValue.getValue() + " ");
                  Platform.runLater(() -> this.tree.getSelectionModel().select(newValue.getParent()));
                } else if (newValue.getParent() == this.lobbiesHeader) {
                  Platform.runLater(() -> {
                    this.textLobbyName.setText(newValue.getValue().split(" ")[0]);
                    this.textLobbyPassword.setText("");
                    this.textLobbyPassword.requestFocus();
                    this.textLobbyPassword.end();
                  });
                }
              }
            });
  }

  /**
   * Sets the lobby list to the given lobby info. Called from the Client class.
   *
   * @param lobbyInfo Has format [[lobbyName, user1, user2, ...], [lobbyName, user1, user2, ...],
   *     ...]
   */
  public void setLobbyList(String[][] lobbyInfo) {
    if (lobbyInfo.length == 0) {
      this.lobbiesHeader.setValue("Lobbies (empty)");
    } else {
      this.lobbiesHeader.setValue("Lobbies");
    }

    this.lobbiesHeader.getChildren().clear();

    for (String[] lobby : lobbyInfo) {
      String lobbyName = lobby[0] + " (" + (lobby.length - 1) + "/4)";
      String[] users = Arrays.copyOfRange(lobby, 1, lobby.length);
      TreeItem<String> lobbyItem = new TreeItem<>(lobbyName);
      this.lobbiesHeader.getChildren().add(lobbyItem);
      for (String user : users) {
        lobbyItem.getChildren().add(new TreeItem<>(user));
      }
    }
  }

  /**
   * Sets the user list to the given users. Called from the Client class.
   * @param users The array of users.
   */
  public void setUsersList(String[] users) {
    if (users.length == 0) {
      this.usersHeader.setValue("Users (empty)");
    } else {
      this.usersHeader.setValue("Users (" + users.length + ")");
    }
    this.usersHeader.getChildren().clear();

    for (String user : users) {
      this.usersHeader.getChildren().add(new TreeItem<>(user.equals(Client.getInstance().getUsername()) ? user + " (you)" : user));
    }

    this.tree.refresh();
  }

  /** Clears the lobby name and password fields. */
  public void clear() {
    if (this.textLobbyName != null && this.textLobbyPassword != null) {
      this.textLobbyName.clear();
      this.textLobbyPassword.clear();
    }
  }
}
