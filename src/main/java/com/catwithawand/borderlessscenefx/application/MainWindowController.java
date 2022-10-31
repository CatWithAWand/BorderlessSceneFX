package com.catwithawand.borderlessscenefx.application;

import com.catwithawand.borderlessscenefx.scene.BorderlessScene;
import com.catwithawand.borderlessscenefx.window.TransparentWindow.TransparentWindowStyle;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;

public class MainWindowController {

  private BorderlessScene borderlessScene;

  @FXML
  private HBox topBar;

  @FXML
  private Button minimizeBtn;

  @FXML
  private Button maximizeBtn;

  @FXML
  private Button closeBtn;

  @FXML
  private Button whiteNoiseStyleBtn;

  @FXML
  private Button glassGlareStyleBtn;

  @FXML
  private Button glassTransparentStyleBtn;

  @FXML
  private Button gnomeStyleBtn;

  @FXML
  private Button minimalisticStyleBtn;

  @FXML
  private Button noneStyleBtn;

  @FXML
  private RadioButton aeroSnapSelect;

  @FXML
  private RadioButton doubleClickMaximizeSelect;

  @FXML
  private RadioButton resizableSelect;

  @FXML
  private RadioButton verticalResizeSnapSelect;

  @FXML
  private RadioButton preventMouseOverTaskbarSelect;

  @FXML
  private Button debugBtn;

  @FXML
  private Label maximizedLabel;

  @FXML
  private Label snappedLabel;

  @FXML
  private Label widthLabel;

  @FXML
  private Label heightLabel;

  /**
   * Constructor.
   */
  public MainWindowController() {
  }

  /**
   * Checking the functionality of the Borderless Scene library
   */
  public void init() {
    // Make our stage movable by dragging the top bar
    borderlessScene.setMoveControl(topBar);

    // Action buttons
    minimizeBtn.setOnAction(event -> borderlessScene.setMinimized(!Main.borderlessScene.isMinimized()));
    maximizeBtn.setOnAction(event -> borderlessScene.setMaximized(!Main.borderlessScene.isMaximized()));
    closeBtn.setOnAction(event -> borderlessScene.getWindow().hide());

    // Aero snap window styling buttons
    whiteNoiseStyleBtn.setOnAction(event -> {
      borderlessScene.setTransparentWindowStyle(TransparentWindowStyle.WHITE_NOISE);
    });
    glassGlareStyleBtn.setOnAction(event -> {
      borderlessScene.setTransparentWindowStyle(TransparentWindowStyle.GLASS_GLARE);
    });
    glassTransparentStyleBtn.setOnAction(event -> {
      borderlessScene.setTransparentWindowStyle(TransparentWindowStyle.GLASS_TRANSPARENT);
    });
    gnomeStyleBtn.setOnAction(event -> {
      borderlessScene.setTransparentWindowStyle(TransparentWindowStyle.GNOME);
    });
    minimalisticStyleBtn.setOnAction(event -> {
      borderlessScene.setTransparentWindowStyle(TransparentWindowStyle.MINIMALISTIC);
    });
    noneStyleBtn.setOnAction(event -> {
      borderlessScene.setTransparentWindowStyle(TransparentWindowStyle.NONE);
    });

    // Bindings
    aeroSnapSelect.selectedProperty().bindBidirectional(borderlessScene.aeroSnapProperty());
    doubleClickMaximizeSelect.selectedProperty()
        .bindBidirectional(borderlessScene.doubleClickMaximizableProperty());
    resizableSelect.selectedProperty().bindBidirectional(borderlessScene.resizableProperty());
    verticalResizeSnapSelect.selectedProperty()
        .bindBidirectional(borderlessScene.verticalResizeSnapProperty());
    preventMouseOverTaskbarSelect.selectedProperty()
        .bindBidirectional(borderlessScene.preventMouseOverTaskbarProperty());
    maximizedLabel.textProperty().bind(Bindings.createStringBinding(
        () -> "Maximized: " + borderlessScene.maximizedProperty().get(),
        borderlessScene.maximizedProperty()
    ));
    snappedLabel.textProperty().bind(Bindings.createStringBinding(
        () -> "Snapped: " + borderlessScene.snappedProperty().get(),
        borderlessScene.snappedProperty()
    ));

    widthLabel.textProperty().bind(Bindings.createStringBinding(
        () -> "Width: " + borderlessScene.widthProperty().get(),
        borderlessScene.widthProperty()
    ));

    heightLabel.textProperty().bind(Bindings.createStringBinding(
        () -> "Height: " + borderlessScene.heightProperty().get(),
        borderlessScene.heightProperty()
    ));

    // Debug button
    debugBtn.setOnAction(event -> {
      whiteNoiseStyleBtn.setDisable(true);
      glassGlareStyleBtn.setDisable(true);
      glassTransparentStyleBtn.setDisable(true);
      gnomeStyleBtn.setDisable(true);
      minimalisticStyleBtn.setDisable(true);
      noneStyleBtn.setDisable(true);
      borderlessScene.debug();
    });
  }

  /**
   * @param borderlessScene The borderless scene to set, instance of {@link BorderlessScene}
   */
  public void setBorderlessScene(BorderlessScene borderlessScene) {
    this.borderlessScene = borderlessScene;
  }

}
