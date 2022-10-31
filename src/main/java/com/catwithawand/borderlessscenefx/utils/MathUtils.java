package com.catwithawand.borderlessscenefx.utils;

public class MathUtils {

  public static Double clamp(Double value, Double min, Double max) {
    return Math.min(Math.max(value, min), max);
  }

}
