package gui;

import java.awt.*;import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiJavaFX extends Application {

  private double WIDTH = (int) Screen.getPrimary().getBounds().getWidth();
  private double HEIGHT = (int) Screen.getPrimary().getBounds().getHeight();
  private double scalingFactor = 1;

  Group root = new Group();
  private Stage stage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Create a black scene depending on the screen resolution
    Scene scene = initScene();

    // Set stage scene
    this.stage = primaryStage;
    this.stage.setTitle("Hues in the Air");
    this.stage.setScene(scene);

    Text title = new Text();
    title.setText("Hues in the Air");
    title.setFont(Font.font("Verdana", 50));
    title.setFill(Colours.WHITE.getHex());
    title.setX(this.WIDTH / 2 - title.getLayoutBounds().getWidth() / 2);
    title.setY(this.HEIGHT / 3);

    root.getChildren().add(title);

    // Set stage properties
    this.stage.setOnCloseRequest(e -> {
      e.consume();
      this.handleEscape();
    });

    //this.stage.setFullScreen(true);
    this.stage.setResizable(false);
    this.stage.getIcons().add(new Image("images/logo.jpg"));
    this.stage.show();
  }


  /**
   * Creates a black scene depending on the screen resolution.
   * The scene has a width of 16:9 and is scaled to fit the screen.
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

    return new Scene(this.root, this.WIDTH, this.HEIGHT, Colours.BLACK.getHex());
  }
  /**
   * The user has pressed the escape key or clicked the close button. A dialog is shown to confirm
   * the user's intention to exit the game.
   */
  public void handleEscape() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Hues in the Air");

    // Add logo
    ImageView logo = new ImageView(new Image("images/logo.jpg"));
    logo.setFitHeight(50);
    logo.setFitWidth(50);
    alert.setGraphic(logo);

    alert.setHeaderText("Are you sure you want to exit the game?");
    alert.initOwner(this.stage);
    alert.initStyle(StageStyle.UNDECORATED);

    Optional<ButtonType> result = alert.showAndWait();
    if (result.orElse(null) == ButtonType.OK) {
      stage.close();
    }
  }
}
