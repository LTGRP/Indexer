package com.billooms.indexwheeleditor;


import java.util.Hashtable;
import javax.swing.JLabel;

/**
 * Create labels for sliders
 * @author Bill Ooms Copyright (c) 2009 Studio of Bill Ooms all rights reserved
 */
public class SliderLabels extends Hashtable {

	/**
	 * Create custom labels for a slider.
	 * @param sliderMin minimum slider value
	 * @param sliderMax maximum slider value
	 * @param sliderDelta delta for putting labels (in slider units)
	 * @param labelMin starting label value
	 * @param labelDelta increment for label values
	 */
	public SliderLabels(int sliderMin, int sliderMax, int sliderDelta, int labelMin, int labelDelta) {
		int i, n;
		for (i = sliderMin, n = labelMin; i <= sliderMax; i = i+sliderDelta, n = n+labelDelta) {
			put(new Integer(i), new JLabel(Integer.toString(n)));
		}
	}

	/**
	 * Create custom labels for a slider.
	 * @param sliderMin minimum slider value
	 * @param sliderMax maximum slider value
	 * @param sliderDelta delta for putting labels (in slider units)
	 * @param labelMin starting label value
	 * @param labelDelta increment for label values
	 */
	public SliderLabels(int sliderMin, int sliderMax, int sliderDelta, double labelMin, double labelDelta) {
		int i;
		double n;
		for (i = sliderMin, n = labelMin; i <= sliderMax; i = i+sliderDelta, n = n+labelDelta) {
			put(new Integer(i), new JLabel(Double.toString(n)));
		}
	}

}
