package com.catwithawand.borderlessscenefx.geometry;

/**
 * A class that represents a delta between two points.
 */
public class Delta {

	Double x;
	Double y;

	public Delta() {

	}

	public Delta(Double x,  Double y) {
		this.x = x;
		this.y = y;
	}

	public Double getX() {
		return x;
	}

	public void setX(Double x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}

}
