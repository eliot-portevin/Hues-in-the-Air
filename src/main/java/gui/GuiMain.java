package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;import javafx.stage.Stage;
import java.io.IOException;

public class GuiMain extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    // Load fonts
    Font.loadFont(getClass().getResourceAsStream("/fonts/Raleway.ttf"), 100);
    Font.loadFont(getClass().getResourceAsStream("/fonts/Bebas_Neue_Regular.ttf"), 20);

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/menu.fxml"));
    Scene scene = new Scene(loader.load());
    stage.setTitle("Hues in the Air");
    stage.setScene(scene);
    stage.show();
  }
  public static void main(String[] args) {
    Application.launch();
  }
}
