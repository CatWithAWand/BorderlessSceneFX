package com.catwithawand.borderlessscenefx.geometry;

/**
 * A class representing a dimension.
 */
public class Dimension {

  Double width;
  Double height;

  public Dimension() {

  }

  public Dimension(Double width,  Double height) {
    this.width = width;
    this.height = height;
  }

  public Double getWidth() {
    return width;
  }

  public void setWidth(Double width) {
    this.width = width;
  }

  public Double getHeight() {
    return height;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

}
