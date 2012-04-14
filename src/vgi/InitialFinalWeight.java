/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi;

import java.awt.geom.Point2D;
import java.text.NumberFormat;

/**
 *
 * @author JLiu
 */
public class InitialFinalWeight {

	public static class GeometricData {

		public Point2D.Double offset;

		public GeometricData() {
			this.offset = null;
		}
	}  // End public static class GeometricData
	protected Object value;
	protected GeometricData geometricData;

	public InitialFinalWeight() {
		this.value = null;
		this.geometricData = null;
	}  // End public InitialFinalWeight()

	public InitialFinalWeight(Object value) {
		this();
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public GeometricData getGeometricData() {
		return this.geometricData;
	}

	public void setGeometricData(GeometricData geometricData) {
		this.geometricData = geometricData;
	}

	@Override
	public String toString() {
		if (this.value instanceof Double) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setGroupingUsed(false);
			return numberFormat.format((Double) this.value);
		}
		return this.value.toString();
	}  // End public String toString()
}  // End public class InitialFinalWeight 
