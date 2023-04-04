package gui;

import java.awt.*;
import java.io.IOException;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiJavaFX extends Application {

  // Screen resolution
  private double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
  private double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
  private double scalingFactor = 1;

  private GridPane root = new GridPane();
  private Stage stage;

  private boolean fullscreen = false;

  /**
   * Starts the application by creating a scene and setting the stage properties.
   * Then proceeds to set the scene as the login screen.
   *
   * @throws Exception
   */

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Create a black scene depending on the screen resolution
    Scene scene = initScene();

    // Set stage scene
    this.stage = primaryStage;
    this.stage.setTitle("Hues in the Air");
    this.stage.setScene(scene);

    this.loadLoginScreen();

    // Set stage properties
    this.stage.setOnCloseRequest(
        e -> {
          e.consume();
          this.handleEscape();
        });

    //this.stage.setFullScreen(true);
    //this.fullscreen = true;
    this.stage.setResizable(true);
    this.stage.show();
    System.out.printf("Width: %f, Height: %f, Scaling factor: %f", this.WIDTH, this.HEIGHT, this.scalingFactor);
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

    this.HEIGHT = width / 16 * 9;
    this.WIDTH = width;

    this.scalingFactor = width / 1920;
    if (this.scalingFactor > 1) {
      this.scalingFactor = height / 1080;
      this.HEIGHT = height;
      this.WIDTH = height / 9 * 16;
    }

    this.scalingFactor *= 0.8;

    return new Scene(this.root, this.WIDTH, this.HEIGHT);
  }

  /**
   * The user has pressed the escape key or clicked the close button. A dialog is shown to confirm
   * the user's intention to exit the game.
   */
  private void handleEscape() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Hues in the Air");

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
     * @throws IOException
     */
  private void loadLoginScreen() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/LoginPage.fxml"));
    this.root = loader.load();
    this.scaleRoot();

    // Set the scene
    this.stage.getScene().setRoot(this.root);
  }

  private void scaleRoot() {
    // Scale the root
    Scale scale = new Scale(this.scalingFactor, this.scalingFactor);
    scale.setPivotX(0);
    scale.setPivotY(0);
    //this.root.getTransforms().add(scale);
  }
}
