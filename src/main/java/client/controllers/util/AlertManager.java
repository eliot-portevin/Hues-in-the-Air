package client.controllers.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AlertManager {

  private final HBox alertPane;
  private final Label alert;
  private FadeTransition alertTransition;

  public AlertManager(HBox alertPane, Label alert) {
    this.alertPane = alertPane;
    this.alert = alert;

    Platform.runLater(this::setAlert);
  }

  /**
   * Creates a fade transition for the alert label. This label is used to display messages to the
   * client, such as confirmation of a successful action or an error.
   */
  public void setAlert() {
    alert
        .styleProperty()
        .bind(Bindings.concat("-fx-font-size: ", alertPane.widthProperty().divide(30)));
    alert.setOpacity(0.0);
    alertTransition = new FadeTransition(Duration.millis(5000), alertPane);
    alertTransition.setFromValue(1.0);
    alertTransition.setToValue(0.0);
    alertTransition.setCycleCount(1);
    alertTransition.setAutoReverse(false);
  }

  /**
   * Displays an alert message to the client.
   *
   * @param message The message to display
   * @param isError Whether the message should be red or not
   */
  public void displayAlert(String message, Boolean isError) {
    // move the alert to the front
    Platform.runLater(
        () -> {
          alertPane.toFront();
          alert.setText(message);
          alert.setTextFill(isError ? Color.valueOf("#ff0000") : Color.valueOf("#ffffff"));
          alert.setOpacity(1.0);
          alertTransition.playFromStart();
        });
  }
}
