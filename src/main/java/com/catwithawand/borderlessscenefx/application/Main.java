package com.catwithawand.borderlessscenefx.application;

import com.catwithawand.borderlessscenefx.scene.BorderlessScene;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * Demo application to showcase the borderless scene
 */
public class Main extends Application {

  static BorderlessScene borderlessScene;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {

    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
    Parent root = fxmlLoader.load();
    MainWindowController mainWindowController = fxmlLoader.getController();

    // Create your borderless scene
    borderlessScene = new BorderlessScene(
        primaryStage,
        StageStyle.TRANSPARENT,
        root,
        Color.TRANSPARENT
    );

    // Add your stylesheet
   borderlessScene.getStylesheets()
       .add(getClass().getResource("/css/demo.css").toExternalForm());

    // mainWindowController
    mainWindowController.setBorderlessScene(borderlessScene);
    mainWindowController.init();

    // Set the scene to your stage
    primaryStage.setScene(borderlessScene);

    primaryStage.setTitle("BorderlessSceneFX Demo");
    primaryStage.setWidth(900);
    primaryStage.setHeight(500);
    primaryStage.setMinWidth(660);
    primaryStage.setMinHeight(495);

    // Show the stage
    primaryStage.show();
  }

}
