package com.catwithawand.borderlessscenefx.scene;

import com.catwithawand.borderlessscenefx.utils.MathUtils;
import com.catwithawand.borderlessscenefx.utils.OsUtils;
import com.catwithawand.borderlessscenefx.utils.WindowInstance;
import com.catwithawand.borderlessscenefx.geometry.Delta;
import com.catwithawand.borderlessscenefx.geometry.Direction;
import com.catwithawand.borderlessscenefx.geometry.HDirection;
import com.catwithawand.borderlessscenefx.geometry.VDirection;
import com.sun.jna.platform.win32.WinUser;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Stage;
import com.catwithawand.borderlessscenefx.window.TransparentWindow;
import com.catwithawand.borderlessscenefx.geometry.Dimension;

/**
 * Controller for the borderless scene.
 * Used internally by {@code BorderlessSceneFX}.
 */
public class BorderlessController {

  protected final Dimension prevSize = new Dimension(
      Double.NEGATIVE_INFINITY,
      Double.NEGATIVE_INFINITY
  );
  protected final Delta prevPos = new Delta(
      Double.NEGATIVE_INFINITY,
      Double.NEGATIVE_INFINITY
  );
  private final BorderlessScene scene;
  private final Stage stage;
  private boolean isMaximizedFromSnap = false;

  @FXML
  private Pane leftPane;

  @FXML
  private Pane rightPane;

  @FXML
  private Pane topPane;

  @FXML
  private Pane bottomPane;

  @FXML
  private Pane topLeftPane;

  @FXML
  private Pane topRightPane;

  @FXML
  private Pane bottomLeftPane;

  @FXML
  private Pane bottomRightPane;

  /**
   * Transparent Window used to as visual feedback for window snapping
   */
  private TransparentWindow transparentWindow;

  /**
   * An object holding our window handle used to enable window animations for Windows OS
   */
  private WindowInstance windowInstance;

  /**
   * Constructor.
   *
   * @param stage The Stage the borderless scene is attached to.
   * @param scene The BorderlessScene that this controller is associated with.
   */
  protected BorderlessController(Stage stage, BorderlessScene scene) {
    this.stage = stage;
    this.scene = scene;

    stage.setOnShown(windowEvent -> {
      updatePrevSizeAndPos();

      if (OsUtils.IS_WINDOWS) {
        windowInstance = new WindowInstance();
      }
    });

    stage.iconifiedProperty().addListener((observableValue, aBoolean, t1) -> {
      // Update minimize property in case the user minimizes/unminimizes the stage via
      // the taskbar
      scene.setMinimized(t1);
    });
  }

  protected void createTransparentWindow() {
    transparentWindow = new TransparentWindow();
    transparentWindow.getStage().initOwner(stage);
  }

  protected void destroyTransparentWindow() {
    transparentWindow.destroy();
    transparentWindow = null;
  }

  protected TransparentWindow getTransparentWindow() {
    return transparentWindow;
  }

  @FXML
  private void initialize() {
    setResizeControl(topRightPane, Direction.TOP_RIGHT);
    setResizeControl(topPane, Direction.TOP);
    setResizeControl(topLeftPane, Direction.TOP_LEFT);
    setResizeControl(rightPane, Direction.RIGHT);
    setResizeControl(leftPane, Direction.LEFT);
    setResizeControl(bottomRightPane, Direction.BOTTOM_RIGHT);
    setResizeControl(bottomPane, Direction.BOTTOM);
    setResizeControl(bottomLeftPane, Direction.BOTTOM_LEFT);

    BooleanBinding allowResizingBind = Bindings.createBooleanBinding(
        () -> !scene.isResizable() || scene.isMaximized(),
        scene.resizableProperty(),
        scene.maximizedProperty()
    );

    topRightPane.disableProperty().bind(allowResizingBind);
    topPane.disableProperty().bind(allowResizingBind);
    topLeftPane.disableProperty().bind(allowResizingBind);
    rightPane.disableProperty().bind(allowResizingBind);
    leftPane.disableProperty().bind(allowResizingBind);
    bottomRightPane.disableProperty().bind(allowResizingBind);
    bottomPane.disableProperty().bind(allowResizingBind);
    bottomLeftPane.disableProperty().bind(allowResizingBind);
  }

  /**
   * Maximize/unmaximize the stage.
   */
  protected void maximize() {
    // Because this is called via the property invalidate method the value of the property
    // has changed already hence why we evaluate the negation of it
    if (!scene.isMaximized()) {
      revertToPreviousSizeAndPos();
    } else {
      if (!scene.isSnapped() && !isMaximizedFromSnap) {
        isMaximizedFromSnap = false;
        updatePrevSizeAndPos();
      }

      Rectangle2D screen;
      ObservableList<Screen> screensIntersectingHalf = getScreensIntersectingHalf();

      if (screensIntersectingHalf.isEmpty()) {
        screen = getScreensIntersectingFull().get(0).getVisualBounds();
      } else {
        screen = screensIntersectingHalf.get(0).getVisualBounds();
      }

      stage.setX(screen.getMinX());
      stage.setY(screen.getMinY());
      stage.setWidth(screen.getWidth());
      stage.setHeight(screen.getHeight());
    }
  }

  /**
   * Minimize/unminimize the stage.
   */
  protected void minimize() {
    if (OsUtils.IS_WINDOWS) {
      // Allows to minimize/unminimize the stage from the taskbar and enables minimize
      // animations for the Windows OS
      int newStyle = windowInstance.oldStyle | WinUser.WS_MINIMIZEBOX | (scene.isMinimized() ?
          WinUser.WS_SYSMENU | WinUser.WS_CAPTION : 0);
      windowInstance.user32.SetWindowLong(windowInstance.hwnd, WinUser.GWL_STYLE, newStyle);
    }

    stage.setIconified(scene.minimizedProperty().get());
  }

  private void updatePrevSizeAndPos() {
    prevSize.setWidth(stage.getWidth());
    prevSize.setHeight(stage.getHeight());
    prevPos.setX(stage.getX());
    prevPos.setY(stage.getY());
  }

  private void revertToPreviousSizeAndPos() {
    stage.setWidth(prevSize.getWidth());
    stage.setHeight(prevSize.getHeight());
    stage.setX(prevPos.getX());
    stage.setY(prevPos.getY());
  }

  private void revertToPreviousSizeAndPosClamped(Rectangle2D screen) {
    Double maxWidth = Math.min(screen.getWidth(), stage.getMaxWidth());
    Double maxHeight = Math.min(screen.getHeight(), stage.getMaxHeight());

    stage.setWidth(MathUtils.clamp(
        prevSize.getWidth(),
        stage.getMinWidth(),
        maxWidth
    ));
    stage.setHeight(MathUtils.clamp(
        prevSize.getHeight(),
        stage.getMinHeight(),
        maxHeight
    ));
    stage.setX(MathUtils.clamp(prevPos.getX(), screen.getMinX(), screen.getMaxX()));
    stage.setY(MathUtils.clamp(prevPos.getY(), screen.getMinY(), screen.getMaxY()));
  }

  private ObservableList<Screen> getScreensIntersectingHalf() {
    return Screen.getScreensForRectangle(
        stage.getX(),
        stage.getY(),
        stage.getWidth() / 2,
        stage.getHeight() / 2
    );
  }

  private ObservableList<Screen> getScreensIntersectingFull() {
    return Screen.getScreensForRectangle(
        stage.getX(),
        stage.getY(),
        stage.getWidth(),
        stage.getHeight()
    );
  }


  /**
   * Set the move control to move the stage with.
   *
   * @param node The node to set as the move control.
   */
  protected void setMoveControl(Node node) {
    final Delta delta = new Delta();
    final Delta eventSource = new Delta();

    // We are using addEventHandler() instead of setOnXXXXX() because the node is known to
    // the user, and they could very likely use the convenience method to override these
    // event handlers. For more information see:
    // https://stackoverflow.com/questions/37821796/difference-between-setonxxx-method-and-addeventhandler-javafx

    // Record drag deltas on mouse press event
    node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
      if (!event.isPrimaryButtonDown()) {
        return;
      }

      delta.setX(event.getSceneX());
      delta.setY(event.getSceneY());

      if (scene.isMaximized() || scene.isSnapped()) {
        delta.setX(prevSize.getWidth() * (event.getSceneX() / stage.getWidth()));
        delta.setY(prevSize.getHeight() * (event.getSceneY() / stage.getHeight()));
      } else {
        updatePrevSizeAndPos();
      }

      eventSource.setX(event.getScreenX());
      eventSource.setY(node.prefHeight(stage.getHeight()));
    });

    // Dragging moves the stage around and displays window snapping visual feedback if enabled
    node.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
      if (!event.isPrimaryButtonDown()) {
        return;
      }

      // Move x axis
      stage.setX(event.getScreenX() - delta.getX());

      if (scene.isSnapped()) {
        if (event.getScreenY() > eventSource.getY()) {
          snapOff();
        } else {
          Rectangle2D screen = Screen.getScreensForRectangle(
              event.getScreenX(),
              event.getScreenY(),
              1,
              1
          ).get(0).getVisualBounds();
          stage.setHeight(screen.getHeight());
        }
      } else {
        // Move y axis
        stage.setY(event.getScreenY() - delta.getY());
      }

      // Aero snap off
      if (scene.isMaximized()) {
        snapOff();
        scene.setMaximized(false);
      }

      if (!scene.isAeroSnap()) {
        return;
      }

      ObservableList<Screen> screens = Screen.getScreensForRectangle(
          event.getScreenX(),
          event.getScreenY(),
          1,
          1
      );

      if (screens.isEmpty()) {
        return;
      }

      Rectangle2D screen = screens.get(0).getVisualBounds();
      Direction snapDirection = resolveSnapDirection(event, screen);

      if (scene.isPreventMouseOverTaskbar()) {
        limitMouseMovement(screen, event);
      }

      if (snapDirection == null || scene.getDisabledDirections().contains(snapDirection)) {
        transparentWindow.close();
        stage.setAlwaysOnTop(false);
        return;
      }

      Stage transparentWindowStage = transparentWindow.getStage();

      if (snapDirection.equals(Direction.TOP_RIGHT)) {
        transparentWindowStage.setX((screen.getWidth() / 2) + screen.getMinX());
        transparentWindowStage.setY(screen.getMinY());
        transparentWindowStage.setWidth(screen.getWidth() / 2);
        transparentWindowStage.setHeight(screen.getHeight() / 2);
      } else if (snapDirection.equals(Direction.TOP_LEFT)) {
        transparentWindowStage.setX(screen.getMinX());
        transparentWindowStage.setY(screen.getMinY());
        transparentWindowStage.setWidth(screen.getWidth() / 2);
        transparentWindowStage.setHeight(screen.getHeight() / 2);
      } else if (snapDirection.equals(Direction.BOTTOM_RIGHT)) {
        transparentWindowStage.setX((screen.getWidth() / 2) + screen.getMinX());
        transparentWindowStage.setY(screen.getMaxY() - (screen.getHeight() / 2));
        transparentWindowStage.setWidth(screen.getWidth() / 2);
        transparentWindowStage.setHeight(screen.getHeight() / 2);
      } else if (snapDirection.equals(Direction.BOTTOM_LEFT)) {
        transparentWindowStage.setX(screen.getMinX());
        transparentWindowStage.setY(screen.getMaxY() - (screen.getHeight() / 2));
        transparentWindowStage.setWidth(screen.getWidth() / 2);
        transparentWindowStage.setHeight(screen.getHeight() / 2);
      } else if (snapDirection.equals(Direction.RIGHT)) {
        transparentWindowStage.setY(screen.getMinY());
        transparentWindowStage.setHeight(screen.getHeight());
        transparentWindowStage.setWidth(Math.max(
            screen.getWidth() / 2,
            transparentWindowStage.getMinWidth()
        ));
        transparentWindowStage.setX(screen.getMaxX() - transparentWindowStage.getWidth());
      } else if (snapDirection.equals(Direction.LEFT)) {
        transparentWindowStage.setY(screen.getMinY());
        transparentWindowStage.setHeight(screen.getHeight());
        transparentWindowStage.setX(screen.getMinX());
        transparentWindowStage.setWidth(Math.max(
            screen.getWidth() / 2,
            transparentWindowStage.getMinWidth()
        ));
      } else if (snapDirection.equals(Direction.TOP)
          || snapDirection.equals(Direction.BOTTOM)) {
        transparentWindowStage.setX(screen.getMinX());
        transparentWindowStage.setY(screen.getMinY());
        transparentWindowStage.setWidth(screen.getWidth());
        transparentWindowStage.setHeight(screen.getHeight());
      }

      transparentWindow.show();
      // stage.toFront() doesn't seem to work, so we have to do it with stage
      // .setAlwaysOnTop() in a "hackish" way
      stage.setAlwaysOnTop(true);
    });

    // Snap window to position on release.
    node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
      try {
        if (!scene.isAeroSnap() || !event.getButton().equals(MouseButton.PRIMARY)
            || event.getScreenX() == eventSource.getX()) {
          return;
        }

        Rectangle2D screen = Screen.getScreensForRectangle(
            event.getScreenX(),
            event.getScreenY(),
            1,
            1
        ).get(0).getVisualBounds();
        Direction snapDirection = resolveSnapDirection(event, screen);

        if (snapDirection == null || scene.getDisabledDirections().contains(snapDirection)) {
          return;
        }

        if (snapDirection.equals(Direction.TOP) || snapDirection.equals(Direction.BOTTOM)) {
          isMaximizedFromSnap = true;
          scene.setMaximized(true);
        } else {
          Stage transparentWindowStage = transparentWindow.getStage();

          stage.setX(transparentWindowStage.getX());
          stage.setY(transparentWindowStage.getY());
          stage.setWidth(transparentWindowStage.getWidth());
          stage.setHeight(transparentWindowStage.getHeight());
          scene.setSnapped(true);
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        // Close the visual feedback window regardless
        if (transparentWindow != null) {
          transparentWindow.close();
        }
        stage.setAlwaysOnTop(false);
      }
    });

    // Maximize/unmaximize on double click
    node.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
      if (!event.getButton().equals(MouseButton.PRIMARY) || event.getClickCount() < 2
          || !scene.isDoubleClickMaximizable()) {
        return;
      }

      if (scene.isSnapped()) {
        snapOff();
        revertToPreviousSizeAndPosClamped(getScreensIntersectingHalf().get(0)
                                              .getVisualBounds());
        return;
      }

      scene.setMaximized(!scene.isMaximized());
    });
  }

  /**
   * Set pane (anchor points) to resize application when pressed and dragged.
   *
   * @param pane      The pane to set the resize event on.
   * @param direction The resize direction this node handles, instance of {@link Direction}.
   */
  private void setResizeControl(Pane pane, final Direction direction) {
    HDirection hDirection = direction.getHDirection();
    VDirection vDirection = direction.getVDirection();

    //Record the previous size and previous position
    pane.setOnDragDetected(event -> {
      if (!scene.isSnapped()) {
        updatePrevSizeAndPos();
      }
    });

    //TODO(Ariana): eliminate content jitter when resizing from the
    // top/top-left/left/bottom-left/top-right

    pane.setOnMouseDragged(event -> {
      if (!event.isPrimaryButtonDown()) {
        return;
      }

      final double width = stage.getWidth();
      final double height = stage.getHeight();
      Rectangle2D screen = getScreensIntersectingHalf().get(0).getVisualBounds();

      if (scene.isPreventMouseOverTaskbar()) {
        limitMouseMovement(screen, event);
      }

      // Horizontal resize
      if (hDirection != null) {
        double comingWidth = hDirection.equals(HDirection.LEFT) ? width - event.getScreenX()
            + stage.getX() : width + event.getX();

        if (comingWidth <= 0 || comingWidth < stage.getMinWidth()
            || comingWidth > stage.getMaxWidth()) {
          return;
        }

        if (hDirection.equals(HDirection.LEFT)) {
          stage.setWidth(stage.getX() - event.getScreenX() + width);
          stage.setX(event.getScreenX());
        } else {
          stage.setWidth(event.getSceneX());
        }
      }

      // Vertical resize
      if (vDirection != null) {
        if (scene.isSnapped() && !(stage.getX() <= screen.getMinX()
            || (stage.getX() + width) >= screen.getMaxX())) {
          stage.setHeight(prevSize.getHeight());
          scene.setSnapped(false);
        }

        double comingHeight = vDirection.equals(VDirection.TOP) ? height - event.getScreenY()
            + stage.getY() : height + event.getY();

        if (comingHeight <= 0 || comingHeight < stage.getMinHeight()
            || comingHeight > stage.getMaxHeight()) {
          return;
        }

        if (vDirection.equals(VDirection.TOP)) {
          stage.setHeight(stage.getY() - event.getScreenY() + height);
          stage.setY(event.getScreenY());
        } else {
          stage.setHeight(event.getSceneY());
        }

        if (scene.isAeroSnap() && scene.isVerticalResizeSnap()) {
          if (!isLegalVerticalResizeSnap(screen, vDirection, event)) {
            transparentWindow.close();
            stage.setAlwaysOnTop(false);
            return;
          }

          Stage transparentWindowStage = transparentWindow.getStage();

          transparentWindowStage.setWidth(stage.getWidth());
          transparentWindowStage.setHeight(screen.getHeight());
          transparentWindowStage.setX(stage.getX());
          transparentWindowStage.setY(screen.getMinY());

          transparentWindow.show();
          stage.setAlwaysOnTop(true);
        }
      }
    });

    // Capture stage dimensions and position when the move node is pressed
    pane.setOnMousePressed(event -> {
      if (!event.isPrimaryButtonDown() || scene.isSnapped()) {
        return;
      }

      updatePrevSizeAndPos();
    });

    // Aero Snap during vertical resizing
    pane.setOnMouseReleased(event -> {
      if (vDirection == null || !scene.isVerticalResizeSnap() || !event.getButton()
          .equals(MouseButton.PRIMARY)) {
        return;
      }

      Rectangle2D screen = Screen.getScreensForRectangle(
          event.getScreenX(),
          event.getScreenY(),
          1,
          1
      ).get(0).getVisualBounds();

      if (isLegalVerticalResizeSnap(screen, vDirection, event)) {
        stage.setY(screen.getMinY());
        stage.setHeight(screen.getHeight());
        scene.setSnapped(true);
      }

      if (scene.isAeroSnap()) {
        transparentWindow.close();
        stage.setAlwaysOnTop(false);
      }
    });

    // Aero snap resize on double click
    pane.setOnMouseClicked(event -> {
      if (!(event.getButton().equals(MouseButton.PRIMARY)) || (event.getClickCount() < 2) || (
          vDirection == null)) {
        return;
      }

      if (scene.isSnapped()) {
        stage.setHeight(prevSize.getHeight());
        stage.setY(prevPos.getY());
        scene.setSnapped(false);
      } else {
        Rectangle2D screen = getScreensIntersectingHalf().get(0).getVisualBounds();

        prevSize.setHeight(stage.getHeight());
        prevPos.setY(stage.getY());
        stage.setHeight(screen.getHeight());
        stage.setY(screen.getMinY());
        scene.setSnapped(true);
      }
    });
  }

  private Direction resolveSnapDirection(MouseEvent event, Rectangle2D screen) {
    Double allowance = scene.getAeroSnapAllowance();
    Double cornerAllowance = scene.getAeroSnapCornerAllowance();

    if (event.getScreenY() <= screen.getMinY() + cornerAllowance
        && event.getScreenX() >= screen.getMaxX() - cornerAllowance) {
      // Window snap top right corner
      return Direction.TOP_RIGHT;
    }

    if (event.getScreenY() <= screen.getMinY() + cornerAllowance
        && event.getScreenX() <= screen.getMinX() + cornerAllowance) {
      // Window snap top left corner
      return Direction.TOP_LEFT;
    }

    if (event.getScreenY() >= screen.getMaxY() - cornerAllowance
        && event.getScreenX() >= screen.getMaxX() - cornerAllowance) {
      // Window snap bottom right corner
      return Direction.BOTTOM_RIGHT;
    }

    if (event.getScreenY() >= screen.getMaxY() - cornerAllowance
        && event.getScreenX() <= screen.getMinX() + cornerAllowance) {
      // Window snap bottom left corner
      return Direction.BOTTOM_LEFT;
    }

    if (event.getScreenX() >= screen.getMaxX() - allowance) {
      // Window snap right
      return Direction.RIGHT;
    }

    if (event.getScreenX() <= screen.getMinX() + allowance) {
      // Window snap left
      return Direction.LEFT;
    }

    if (event.getScreenY() <= screen.getMinY() + allowance) {
      // Window snap top
      return Direction.TOP;
    }

    if (event.getScreenY() >= screen.getMaxY() - allowance) {
      // Window snap bottom
      return Direction.BOTTOM;
    }

    return null;
  }

  private void snapOff() {
    stage.setWidth(prevSize.getWidth());
    stage.setHeight(prevSize.getHeight());
    scene.setSnapped(false);
  }

  /**
   * Limits the mouse to move within the visual bounds of the screen.
   */
  private void limitMouseMovement(Rectangle2D screen, MouseEvent event) {
    if (event.getScreenX() >= screen.getMaxX() || event.getScreenY() >= screen.getMaxY()) {
      Robot robot = new Robot();
      double x = Math.min(event.getScreenX(), screen.getMaxX()) - 1;
      double y = Math.min(event.getScreenY(), screen.getMaxY()) - 1;
      robot.mouseMove(x, y);
    }
  }

  /**
   * Determines whether the window is allowed to snap vertically while resizing.
   */
  private boolean isLegalVerticalResizeSnap(Rectangle2D screen, VDirection vDirection,
      MouseEvent event) {
    double upperBoundary = screen.getMinY() + scene.getAeroSnapAllowance();
    double lowerBoundary = screen.getMaxY() - scene.getAeroSnapAllowance();
    return (((stage.getY() <= upperBoundary && vDirection.equals(VDirection.TOP)) || (
        event.getScreenY() >= lowerBoundary && vDirection.equals(VDirection.BOTTOM))));
  }

}
