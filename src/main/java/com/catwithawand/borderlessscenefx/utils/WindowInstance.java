package com.catwithawand.borderlessscenefx.utils;

import com.sun.glass.ui.Window;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;

/**
 * Class that provides methods to get the window handle of a JavaFX window.
 */
public class WindowInstance {

  public final long lhwnd;
  public final Pointer lpVoid;
  public final HWND hwnd;
  public final User32 user32;
  public final int oldStyle;

  public WindowInstance() {
    lhwnd = Window.getWindows().get(0).getNativeWindow();
    lpVoid = new Pointer(lhwnd);
    hwnd = new HWND(lpVoid);
    user32 = User32.INSTANCE;
    oldStyle = user32.GetWindowLong(hwnd, WinUser.GWL_STYLE);
  }

}
