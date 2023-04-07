package client.controllers;

import client.Client;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Arrays;

public class MenuController {

  public GridPane backgroundPane;

  public Button buttonCreateLobby;
  public Button buttonJoinLobby;

  public ToggleButton tabGames;
  public ToggleButton tabHome;
  public ToggleButton tabSettings;

  public TextField textLobbyName;
  public PasswordField textLobbyPassword;

  public Label labelLobbies;
  public TreeView<String> tree;
  private final TreeItem<String> root = new TreeItem<>("");
  private final TreeItem<String> lobbiesHeader = new TreeItem<>("Lobbies");
  private final TreeItem<String> usersHeader = new TreeItem<>("Users");

  public TextArea chat;
  public TextField textChat;

  @FXML
  public void initialize() {
    // Change text field when <Tab> or <Enter> is clicked
    this.setTabBehaviour();

    // Set behaviour for the login button
    this.setButtonBehaviour();

    // Automatically change font sizes with window size
    this.setFontBehaviour();

    // Set the behaviour for the tab pane
    this.setTabPaneBehaviour();

    // Initialise lobby list
    this.initialiseLobbyList();

    // Initialise chat
    this.initialiseChat();

    this.textLobbyName.setFocusTraversable(false);
    this.textLobbyPassword.setFocusTraversable(false);
    this.buttonCreateLobby.setFocusTraversable(false);
    this.buttonJoinLobby.setFocusTraversable(false);
  }

  private void initialiseChat() {
    this.textChat.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("ENTER")) {
            Client.getInstance().sendMessageServer(this.textChat.getText());
            this.textChat.clear();
          }
        });
  }

  /**
   * Adds the lobbies and users headers to the lobby list.
   */
  private void initialiseLobbyList() {
    this.tree.setRoot(this.root);
    this.tree.setShowRoot(false);
    this.root.getChildren().add(this.lobbiesHeader);
    this.root.getChildren().add(this.usersHeader);
    this.lobbiesHeader.setExpanded(true);
  }

  public void setLobbyList(String[][] lobbyInfo) {
    if (lobbyInfo.length == 0) {
      this.lobbiesHeader.setValue("Lobbies (empty)");
    } else {
      this.lobbiesHeader.setValue("Lobbies");
    }

    this.lobbiesHeader.getChildren().clear();

    for (String[] lobby : lobbyInfo) {
      String lobbyName = lobby[0];
      String[] users = Arrays.copyOfRange(lobby, 1, lobby.length);
      TreeItem<String> lobbyItem = new TreeItem<>(lobbyName);
      this.lobbiesHeader.getChildren().add(lobbyItem);
      for (String user : users) {
        lobbyItem.getChildren().add(new TreeItem<>(user));
      }
    }
  }

  public void setUsersList(String[] users) {
    if (users.length == 0) {
      this.usersHeader.setValue("Users (empty)");
    } else {
      this.usersHeader.setValue("Users");
    }
    this.usersHeader.getChildren().clear();

    for (String user : users) {
      this.usersHeader.getChildren().add(new TreeItem<>(user));
    }
  }

  private void setTabBehaviour() {
    textLobbyName.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB")) {
            textLobbyPassword.requestFocus();
          }
        });
    textLobbyPassword.setOnKeyPressed(
        e -> {
          if (e.getCode().toString().equals("TAB")) {
            buttonCreateLobby.requestFocus();
          }
        });
    buttonCreateLobby.setOnKeyPressed(
        e -> {
          switch (e.getCode().toString()) {
            case "TAB" -> buttonJoinLobby.requestFocus();
            case "ENTER" -> buttonCreateLobby.fire();
          }
        });
    buttonJoinLobby.setOnKeyPressed(
        e -> {
          switch (e.getCode().toString()) {
            case "TAB" -> textLobbyName.requestFocus();
            case "ENTER" -> buttonJoinLobby.fire();
          }
        });
  }

  private void setButtonBehaviour() {
    buttonCreateLobby.setOnAction(
        e -> {
          Client.getInstance().createLobby(textLobbyName.getText(), textLobbyPassword.getText());
          backgroundPane.requestFocus();
        });
    buttonJoinLobby.setOnAction(
        e -> {
          Client.getInstance().joinLobby(textLobbyName.getText(), textLobbyPassword.getText());
          backgroundPane.requestFocus();
        });

    buttonCreateLobby.setOnMouseEntered(e -> Client.getInstance().clickSound());
    buttonJoinLobby.setOnMouseEntered(e -> Client.getInstance().clickSound());

    for (ToggleButton tab : Arrays.asList(tabHome, tabGames, tabSettings)) {
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
    labelLobbies
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(50)));
    // Bind font size of the lobby list to the width of the window
    tree
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(60)));
  }

  /**
   * Selects the home tab for the startup. Also sets the behaviour for the tabs, so that only one
   * tab can be selected at a time.
   */
  private void setTabPaneBehaviour() {
    for (ToggleButton tab : Arrays.asList(tabHome, tabGames, tabSettings)) {
      tab.setOnAction(
          e -> {
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
   *
   * @param tab The ToggleButton to configure
   * @param isSelected Whether the tab should be selected or not
   */
  private void configureTab(ToggleButton tab, boolean isSelected) {
    int fontSize = isSelected ? 45 : 50;
    tab.styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", backgroundPane.widthProperty().divide(fontSize)));
    tab.setSelected(isSelected);
  }

  public void receiveMessage(String message, String sender) {
    this.chat.appendText(String.format("[%s]: %s%n", sender, message));
  }
}
