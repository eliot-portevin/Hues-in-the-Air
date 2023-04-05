package gui;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;

import client.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiJavaFX extends Application {

  private GridPane root = new GridPane();
  private Stage stage;

  /**
   * Starts the application by creating a scene and setting the stage properties. Then proceeds to
   * set the scene as the login screen.
   */
  @Override
  public void start(Stage primaryStage) {
    // TODO: Error appears when setting fullscreen on mac. This is due to the menu bar. Fix?

    // Create a black scene depending on the screen resolution
    Scene scene = initScene();
    scene.getStylesheets().add(getClass().getResource("/layout/FontStyle.css").toExternalForm());

    // Set stage scene
    this.stage = primaryStage;
    this.stage.setTitle("Hues in the Air");
    this.stage.setScene(scene);

    try {
      this.loadLoginScreen();
    } catch (IOException e) {
      System.out.println("Could not load login screen. Closing the program.");
      System.exit(1);
    }

    // Set stage properties
    this.stage.setOnCloseRequest(
        e -> {
          e.consume();
          this.handleEscape();
        });

    //this.stage.setFullScreen(true);
    this.stage.setResizable(true);
    this.stage.show();
  }

  /**
   * Creates a black scene depending on the screen resolution. The scene has a width of 16:9 and is
   * scaled to fit the screen.
   *
   * @return scene
   */
  private Scene initScene() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth();
    double height = screenSize.getHeight();

    double HEIGHT = width / 16 * 9;
    // Screen resolution
    double WIDTH = width;

    double scalingFactor = width / 1920;
    if (scalingFactor > 1) {
      HEIGHT = height;
      WIDTH = height / 9 * 16;
    }

    return new Scene(this.root, WIDTH, HEIGHT);
  }

  /**
   * The user has pressed the escape key or clicked the close button. A dialog is shown to confirm
   * the user's intention to exit the game.
   */
  private void handleEscape() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Hues in the Air");

    // Set dialog pane style
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add("/layout/Dialog.css");

    // Add logo
    ImageView logo = new ImageView(new Image("images/logo.jpg"));
    logo.setFitHeight(50);
    logo.setFitWidth(50);
    alert.setGraphic(logo);

    alert.setHeaderText("Are you sure you want to exit the game?");
    alert.initOwner(this.stage);
    alert.initStyle(StageStyle.UNIFIED);
    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.orElse(null) == ButtonType.YES) {
      stage.close();
    }
  }

  /**
   * Loads the login screen from fxml file. Called upon start of the application.
   *
   * @throws IOException if the fxml file could not be loaded (method FXMLLoader.load()).
   */
  private void loadLoginScreen() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/LoginPage.fxml"));
    this.root = loader.load();

    // Set controller
    LoginController controller = loader.getController();

    // Set the scene
    this.stage.getScene().setRoot(this.root);
  }
}
