package com.billooms.indexwheeleditor.drawables;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Interface defining things that are drawable
 * Note that all Drawable objects are defined in 2 dimensional space using XY coordinate system.
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public interface Drawable {

	/**
	 * Get the color of the object
	 * @return color
	 */
	public Color getColor();

	/**
	 * Set the object color
	 * @param c Color c
	 */
	public void setColor(Color c);

	/**
	 * Determine if the object is currently visible
	 * @return true=visible; false=invisible
	 */
	public boolean isVisible();

	/**
	 * Set the visibility of the object
	 * @param v true=visible; false=not drawn
	 */
	public void setVisible(boolean v);

	/**
	 * Paint the object
	 * @param g2d Graphics2D g
	 */
	public void paint(Graphics2D g2d);
}