package client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
  @FXML public Tab tab_home, tab_chat, tab_settings;

  ImageView homeGraphic = new ImageView();

  /**
   * Called to initialize a controller after its root element has been completely processed.
   *
   * @param location The location used to resolve relative paths for the root object, or {@code
   *     null} if the location is not known.
   * @param resources The resources used to localize the root object, or {@code null} if the root
   *     object was not localized.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    homeGraphic.setImage(new Image("/images/penguin.png"));
    homeGraphic.setFitHeight(20);
    homeGraphic.setFitWidth(20);
    System.out.println("Hi there!");
    if (homeGraphic != null) {
      tab_home.setGraphic(homeGraphic);
      tab_chat.setGraphic(homeGraphic);
      tab_settings.setGraphic(homeGraphic);
    }
  }
}
