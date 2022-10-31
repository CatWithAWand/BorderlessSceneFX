package com.catwithawand.borderlessscenefx.geometry;

/**
 * An enum representing a direction.
 * <p>
 *   This enum is used to represent a direction based on the
 *   combination of horizontal and vertical directions.
 * </p>
 */
public enum Direction {

  TOP_LEFT(VDirection.TOP, HDirection.LEFT),
  TOP(VDirection.TOP, null),
  TOP_RIGHT(VDirection.TOP, HDirection.RIGHT),
  LEFT(null, HDirection.LEFT),
  RIGHT(null, HDirection.RIGHT),
  BOTTOM_LEFT(VDirection.BOTTOM, HDirection.LEFT),
  BOTTOM(VDirection.BOTTOM, null),
  BOTTOM_RIGHT(VDirection.BOTTOM, HDirection.RIGHT);

  private final VDirection vDirection;
  private final HDirection hDirection;

  Direction(VDirection vDirection, HDirection hDirection) {
    this.vDirection = vDirection;
    this.hDirection = hDirection;
  }

  public VDirection getVDirection() {
    return vDirection;
  }

  public HDirection getHDirection() {
    return hDirection;
  }

}
