package com.catwithawand.borderlessscenefx.scene;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import com.catwithawand.borderlessscenefx.geometry.Delta;
import com.catwithawand.borderlessscenefx.geometry.Dimension;
import com.catwithawand.borderlessscenefx.geometry.Direction;
import com.catwithawand.borderlessscenefx.window.TransparentWindow;
import com.catwithawand.borderlessscenefx.window.TransparentWindow.TransparentWindowStyle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.StackPane;

/**
 * <p>
 * This class is a wrapper around a {@link Scene} that provides an undecorated scene with
 * the ability to move, resize, minimize, maximize and Aero Snap/Quarter Tile the stage.
 * <p>
 * Usage:
 * <pre>
 * {@code
 *     BorderlessScene scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED, root, 250, 250);
 *     primaryStage.setScene(scene);
 *     scene.setMoveControl(topBar);
 *     primaryStage.setTitle("Draggable and Undecorated JavaFX Window");
 * 		  primaryStage.show();
 * }
 * </pre>
 */
public class BorderlessScene extends Scene {

  public static final String DEFAULT_STYLE_CLASS = "borderless-scene";
  private static final HashSet<StageStyle> ALLOWED_STAGE_STYLES =
      new HashSet<>(Arrays.asList(
          StageStyle.UNDECORATED,
          StageStyle.TRANSPARENT
      ));
  private final HashSet<Direction> disabledDirections = new HashSet<>();
  private BorderlessController controller;
  private Stage stage;
  private BooleanProperty maximized;
  private BooleanProperty minimized;
  private BooleanProperty resizable;
  private BooleanProperty aeroSnap;
  private DoubleProperty aeroSnapAllowance;
  private DoubleProperty aeroSnapCornerAllowance;
  private BooleanProperty doubleClickMaximizable;
  private BooleanProperty verticalResizeSnap;
  private BooleanProperty preventMouseOverTaskbar;
  private ReadOnlyBooleanWrapper snapped;

  /**
   * Creates a new {@link BorderlessScene} with the given parameters.
   *
   * @param stage      The {@link Stage} that this scene is attached to.
   * @param stageStyle The {@link StageStyle} of the stage.
   *                   <p>
   *                   Must be {@link StageStyle#UNDECORATED} or
   *                   {@link StageStyle#TRANSPARENT}.
   *                   If  a different {@link StageStyle} is passed, the
   *                   {@link StageStyle#TRANSPARENT}
   *                   will be used.
   *                   </p>
   * @param parent     The parent {@link Parent} of the scene.
   *                   This is the same as the root parameter in {@link Scene#Scene(Parent)}.
   */
  public BorderlessScene(Stage stage, StageStyle stageStyle, Parent parent) {
    super(parent);
    try {
      controller = new BorderlessController(stage, this);

      setRoot(loadRoot(controller));
      setContent(parent);

      // Defaults
      getRoot().getStyleClass().add(DEFAULT_STYLE_CLASS);
      setMaximized(false);
      setAeroSnap(true);
      resizableProperty().bindBidirectional(stage.resizableProperty());
      setDoubleClickMaximizable(true);
      setVerticalResizeSnap(true);
      disabledDirections.add(Direction.BOTTOM);
      stage.initStyle(ALLOWED_STAGE_STYLES.contains(stageStyle)
                          ? stageStyle
                          : StageStyle.TRANSPARENT);

      // Load default CSS
      getStylesheets().add(BorderlessScene.class.getResource("/css/styles.css")
                               .toExternalForm());

      this.stage = stage;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a new {@link BorderlessScene} with the given parameters.
   *
   * @param stage      The {@link Stage} that this scene is attached to.
   * @param stageStyle The {@link StageStyle} of the stage.
   *                   <p>
   *                   Must be {@link StageStyle#UNDECORATED} or
   *                   {@link StageStyle#TRANSPARENT}.
   *                   If  a different {@link StageStyle} is passed, the
   *                   {@link StageStyle#TRANSPARENT}
   *                   will be used.
   *                   </p>
   * @param parent     The parent {@link Parent} of the scene.
   *                   This is the same as the root parameter in {@link Scene#Scene(Parent)}.
   * @param width      The width of the scene.
   * @param height     The height of the scene.
   */
  public BorderlessScene(Stage stage, StageStyle stageStyle, Parent parent, double width,
      double height) {
    this(stage, stageStyle, parent);
    stage.setWidth(width);
    stage.setHeight(height);
  }

  /**
   * Creates a new {@link BorderlessScene} with the given parameters.
   *
   * @param stage      The {@link Stage} that this scene is attached to.
   * @param stageStyle The {@link StageStyle} of the stage.
   *                   <p>
   *                   Must be {@link StageStyle#UNDECORATED} or
   *                   {@link StageStyle#TRANSPARENT}.
   *                   If  a different {@link StageStyle} is passed, the
   *                   {@link StageStyle#TRANSPARENT}
   *                   will be used.
   *                   </p>
   * @param parent     The parent {@link Parent} of the scene.
   *                   This is the same as the root parameter in {@link Scene#Scene(Parent)}.
   * @param width      The width of the scene.
   * @param height     The height of the scene.
   * @param paint      The paint {@link Paint} to define the background fill of the
   *                   {@link Scene}.
   */
  public BorderlessScene(Stage stage, StageStyle stageStyle, Parent parent, double width,
      double height, Paint paint) {
    this(stage, stageStyle, parent);
    stage.setWidth(width);
    stage.setHeight(height);
    setFill(paint);
  }

  /**
   * Creates a new {@link BorderlessScene} with the given parameters.
   *
   * @param stage      The {@link Stage} that this scene is attached to.
   * @param stageStyle The {@link StageStyle} of the stage.
   *                   <p>
   *                   Must be {@link StageStyle#UNDECORATED} or
   *                   {@link StageStyle#TRANSPARENT}.
   *                   If  a different {@link StageStyle} is passed, the
   *                   {@link StageStyle#TRANSPARENT}
   *                   will be used.
   *                   </p>
   * @param parent     The parent {@link Parent} of the scene.
   *                   This is the same as the root parameter in {@link Scene#Scene(Parent)}.
   * @param paint      The paint {@link Paint} to define the background fill of the
   *                   {@link Scene}.
   */
  public BorderlessScene(Stage stage, StageStyle stageStyle, Parent parent, Paint paint) {
    this(stage, stageStyle, parent);
    setFill(paint);
  }

  public BooleanProperty maximizedProperty() {
    if (maximized == null) {
      maximized = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
          controller.maximize();
        }

        @Override
        public Object getBean() {
          return BorderlessScene.this;
        }

        @Override
        public String getName() {
          return "maximized";
        }
      };
    }

    return maximized;
  }

  public BooleanProperty minimizedProperty() {
    if (minimized == null) {
      minimized = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
          controller.minimize();
        }

        @Override
        public Object getBean() {
          return BorderlessScene.this;
        }

        @Override
        public String getName() {
          return "minimized";
        }
      };
    }

    return minimized;
  }

  public BooleanProperty resizableProperty() {
    if (resizable == null) {
      resizable = new SimpleBooleanProperty(true);
    }

    return resizable;
  }

  public BooleanProperty aeroSnapProperty() {
    if (aeroSnap == null) {
      aeroSnap = new SimpleBooleanProperty() {
        @Override
        protected void invalidated() {
          if (aeroSnapProperty().get() && controller.getTransparentWindow() == null) {
            controller.createTransparentWindow();
          } else if (aeroSnapProperty().not().get()) {
            controller.destroyTransparentWindow();
          }
        }

        @Override
        public Object getBean() {
          return BorderlessScene.this;
        }

        @Override
        public String getName() {
          return "aeroSnap";
        }
      };
    }

    return aeroSnap;
  }

  public DoubleProperty aeroSnapAllowanceProperty() {
    if (aeroSnapAllowance == null) {
      aeroSnapAllowance = new SimpleDoubleProperty(25);
    }

    return aeroSnapAllowance;
  }

  public DoubleProperty aeroSnapCornerAllowanceProperty() {
    if (aeroSnapCornerAllowance == null) {
      aeroSnapCornerAllowance = new SimpleDoubleProperty(50);
    }

    return aeroSnapCornerAllowance;
  }

  public BooleanProperty doubleClickMaximizableProperty() {
    if (doubleClickMaximizable == null) {
      doubleClickMaximizable = new SimpleBooleanProperty(true);
    }

    return doubleClickMaximizable;
  }

  public BooleanProperty verticalResizeSnapProperty() {
    if (verticalResizeSnap == null) {
      verticalResizeSnap = new SimpleBooleanProperty(true);
    }

    return verticalResizeSnap;
  }

  public BooleanProperty preventMouseOverTaskbarProperty() {
    if (preventMouseOverTaskbar == null) {
      preventMouseOverTaskbar = new SimpleBooleanProperty(true);
    }

    return preventMouseOverTaskbar;
  }

  public ReadOnlyBooleanWrapper snappedProperty() {
    if (snapped == null) {
      snapped = new ReadOnlyBooleanWrapper(false);
    }

    return snapped;
  }

  /**
   * Sets the content of the scene.
   *
   * @param parent The parent {@link Parent} of the scene.
   */
  public void setContent(Parent parent) {
    ((AnchorPane) getRoot()).getChildren().set(0, parent);
    AnchorPane.setLeftAnchor(parent, 0.0D);
    AnchorPane.setTopAnchor(parent, 0.0D);
    AnchorPane.setRightAnchor(parent, 0.0D);
    AnchorPane.setBottomAnchor(parent, 0.0D);
  }

  /**
   * Set a node that can be pressed and dragged to move the stage.
   *
   * @param node The node.
   */
  public void setMoveControl(Node node) {
    controller.setMoveControl(node);
  }

  /**
   * Determines whether the stage is maximized or not.
   *
   * @return {@code boolean} - true if maximized otherwise false.
   */
  public boolean isMaximized() {
    return maximized == null ? false : maximized.get();
  }

  /**
   * Sets whether the stage is maximized or not.
   *
   * @param value true to maximize, false to unmaximize.
   */
  public void setMaximized(boolean value) {
    maximizedProperty().set(value);
  }

  /**
   * Determines whether the stage is minimized or not.
   *
   * @return {@code boolean} - true if minimized otherwise false.
   */
  public boolean isMinimized() {
    return minimized == null ? false : minimized.get();
  }

  /**
   * Sets whether the stage is minimized or not.
   *
   * @param value true to minimize, false to unminimize.
   */
  public void setMinimized(boolean value) {
    minimizedProperty().set(value);
  }

  /**
   * Determines whether the stage is resizable.
   *
   * @return {@code boolean} - true if resizable otherwise false.
   */
  public boolean isResizable() {
    return resizable == null ? true : resizable.get();
  }

  /**
   * Sets whether the scene is resizable.
   *
   * @param value true to enable, false to disable.
   */
  public void setResizable(boolean value) {
    resizableProperty().set(value);
  }

  /**
   * Determines whether aero snapping/quarter tiling is enabled.
   *
   * @return {@code boolean} - true if enabled otherwise false.
   */
  public boolean isAeroSnap() {
    return aeroSnap == null ? true : aeroSnap.get();
  }

  /**
   * Sets whether aero snap/quarter tiling is enabled.
   * <p>
   * Aero snap/quarter tiling is enabled by default.
   * When set to false, the transparent window is destroyed.
   * </p>
   *
   * @param value true to enable, false to disable.
   */
  public void setAeroSnap(boolean value) {
    aeroSnapProperty().set(value);
  }

  /**
   * Returns the amount of space needed from the edge to trigger aero snap/quarter tiling.
   *
   * @return {@code Double}
   */
  public Double getAeroSnapAllowance() {
    return aeroSnapAllowance == null ? 10 : aeroSnapAllowance.get();
  }

  /**
   * Sets the amount of space needed from the edge to trigger aero snap/quarter tiling.
   * <p>
   * The default value is 10.
   * </p>
   *
   * @param value The amount of space needed from the edge to trigger aero snap/quarter
   *              tiling.
   */
  public void setAeroSnapAllowance(Double value) {
    aeroSnapAllowanceProperty().set(value);
  }


  /**
   * Returns the amount of space needed from the edge to trigger aero snap/quarter tiling
   * for corners explicitly.
   *
   * @return {@code Double}
   */
  public Double getAeroSnapCornerAllowance() {
    return aeroSnapCornerAllowance == null ? 50 : aeroSnapCornerAllowance.get();
  }

  /**
   * Sets the amount of space needed from the edge to trigger aero snap/quarter tiling
   * for corners explicitly.
   * <p>
   * The default value is 50.
   * </p>
   *
   * @param value The amount of space needed from the edge to trigger aero snap/quarter
   *              tiling for corners explicitly.
   */
  public void setAeroSnapCornerAllowance(Double value) {
    aeroSnapCornerAllowanceProperty().set(value);
  }

  /**
   * Determines whether the stage can be (un)maximized by double-clicking the move control.
   *
   * @return {@code boolean} - true if enabled otherwise false.
   */
  public boolean isDoubleClickMaximizable() {
    return doubleClickMaximizable == null ? true : doubleClickMaximizable.get();
  }

  /**
   * Sets whether the stage can be (un)maximized by double-clicking the move control.
   * <p>
   * Double-click maximization is enabled by default.
   * </p>
   *
   * @param value true to enable, false to disable.
   */
  public void setDoubleClickMaximizable(boolean value) {
    doubleClickMaximizableProperty().set(value);
  }

  /**
   * Determines whether the stage will snap while vertically resizing.
   *
   * @return {@code boolean} - true if enabled otherwise false.
   */
  public boolean isVerticalResizeSnap() {
    return verticalResizeSnap == null ? true : verticalResizeSnap.get();
  }

  /**
   * Sets whether the stage will snap while vertically resizing.
   * <p>
   * Vertical resize snapping is enabled by default.
   * </p>
   *
   * @param value true to enable, false to disable.
   */
  public void setVerticalResizeSnap(boolean value) {
    verticalResizeSnapProperty().set(value);
  }

  /**
   * Determines whether the stage can be moved past (over) the taskbar.
   *
   * @return {@code boolean} - true if enabled otherwise false.
   */
  public boolean isPreventMouseOverTaskbar() {
    return preventMouseOverTaskbar == null ? true : preventMouseOverTaskbar.get();
  }

  /**
   * Sets whether the stage can be moved past (over) the taskbar.
   *
   * @param value true to enable, false to disable.
   */
  public void setPreventMouseOverTaskbar(boolean value) {
    preventMouseOverTaskbarProperty().set(value);
  }

  /**
   * Determines whether the stage is snapped.
   *
   * @return {@code boolean} - true if snapped otherwise false.
   */
  public boolean isSnapped() {
    return snapped == null ? false : snapped.get();
  }

  protected void setSnapped(boolean value) {
    snappedProperty().set(value);
  }

  /**
   * Toggle to maximize/unmaximize the application.
   * <p>
   * Always called with the negation of the current state
   * </p>
   */
  public void maximizeStage() {
    setMaximized(maximizedProperty().not().get());
  }

  /**
   * Toggle to minimize/unminimize the application.
   * <p>
   * Always called with the negation of the current state
   * </p>
   */
  public void minimizeStage() {
    setMinimized(minimizedProperty().not().get());
  }

  /**
   * Returns a HashSet containing/accepting instances of {@link Direction} of which their
   * window snap is disabled.
   *
   * @return The hashset, an instance of {@code HashSet<Direction>} with the
   * current disabled directions.
   */
  public HashSet<Direction> getDisabledDirections() {
    return disabledDirections;
  }

  /**
   * Set a specific direction as disabled for aero snap/quarter tiling.
   *
   * @param direction The direction to disable.
   */
  public void disableDirection(Direction direction) {
    disabledDirections.add(direction);
  }

  /**
   * Gets the size of the stage.
   *
   * @return The size of this stage, an instance of {@link Dimension} with the
   * current width and height.
   */
  public Dimension getStageSize() {
    if (controller.prevSize.getWidth() == 0) {
      controller.prevSize.setWidth(stage.getWidth());
    }
    if (controller.prevSize.getHeight() == 0) {
      controller.prevSize.setHeight(stage.getHeight());
    }

    return controller.prevSize;
  }

  /**
   * Gets the position of the stage.
   *
   * @return The position of this stage, an instance of {@link Delta} with the
   * current x and y coordinates.
   */
  public Delta getStagePosition() {
    if (controller.prevPos.getX() == null) {
      controller.prevPos.setX(stage.getX());
    }
    if (controller.prevPos.getY() == null) {
      controller.prevPos.setY(stage.getY());
    }

    return controller.prevPos;
  }

  /**
   * Apply a pre-defined style to the transparent window.
   * <p>
   * The default value is {@link TransparentWindowStyle#MINIMALISTIC}.
   * <br>
   * The style can be changed at any time.
   * </p>
   *
   * @param style The style of the transparent window, instance of
   *              {@link TransparentWindowStyle}.
   */
  public void setTransparentWindowStyle(TransparentWindowStyle style) {
    TransparentWindow transparentWindow = getTransparentWindow();

    if (transparentWindow != null) {
      transparentWindow.setStyle(style);
    }
  }

  /**
   * The transparent window which allows the library to have aero snap controls
   *
   * @return The transparent window, instance of {@link TransparentWindow} extends
   * {@link StackPane}.
   */
  public TransparentWindow getTransparentWindow() {
    return controller.getTransparentWindow();
  }

  /**
   * Loads the debug CSS for the borderless scene and transparent window.
   */
  public void debug() {
    String cssUrl = BorderlessScene.class.getResource("/css/debug.css").toExternalForm();
    // Our default CSS will always be at index 0
    getStylesheets().set(0, cssUrl);
    getTransparentWindow().getStylesheets().set(0, cssUrl);
  }

  private AnchorPane loadRoot(BorderlessController controller) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BorderlessScene.fxml"));
    loader.setController(controller);
    return loader.load();
  }

}
