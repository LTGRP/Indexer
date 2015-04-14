package com.billooms.indexwheel.drawables;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Abstract definition of a drawable point defined by inch(cm) position.
 * Extend this to define points with a particular visible shape (i.e. Dot, Plus, etc).
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public abstract class Pt implements Drawable {

	/** Default color */
	public final static Color DEFAULT_COLOR = Color.BLACK;
	
	private final static int PT_SIZE = 10;	// default pixel size of a drawn point (Dot, Plus, SquarePt, etc)
	
	/**
	 * Position of the object in inches (or cm)
	 */
	protected Point2D.Double pos;
	
	/**
	 * Color of the object
	 */
	protected Color color = DEFAULT_COLOR;
	
	/**
	 * Visible (i.e. drawn) or not. 
	 */
	protected boolean visible = true;
	
	/**
	 * Fill the shapes (false = hollow shapes)
	 */
	protected boolean fill = true;
	
	/**
	 * Size of the object in pixels
	 */
	protected int ptSize = PT_SIZE;

	/**
	 * A drawable point defined by inch position
	 * @param pos point position in inches
	 * @param c Color
	 */
	public Pt(Point2D.Double pos, Color c) {
		this.pos = pos;
		this.color = c;
	}

	/**
	 * A drawable point defined by inch position, default color
	 * @param pos point position in inches
	 */
	public Pt(Point2D.Double pos) {
		this.pos = pos;
	}

	/**
	 * Get the point position in inches
	 * @return point position in inches
	 */
	public Point2D.Double getPos() {
		return pos;
	}

	/**
	 * Move the point to a new point defined in inches
	 * @param pos new point in inches
	 */
	public void moveTo(Point2D.Double pos) {
		this.pos = pos;
	}

	/**
	 * Get the color of the object
	 * @return color
	 */
	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * Set the point color
	 * @param c Color c
	 */
	@Override
	public void setColor(Color c) {
		this.color = c;
	}

	/**
	 * Determine if the object is currently visible
	 * @return true=visible; false=invisible
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set the visibility of the object
	 * @param v true=visible; false=not drawn
	 */
	@Override
	public void setVisible(boolean v) {
		visible = v;
	}

	/**
	 * Get the fill parameter
	 * @return fill
	 */
	public boolean getFill() {
		return fill;
	}

	/**
	 * Set the fill of the object
	 * @param f true=filled; false=hollow
	 */
	public void setFill(boolean f) {
		fill = f;
	}

	/**
	 * Get the size of the point in inches
	 * @param g2d Graphics2D
	 * @return size of the point in inches
	 */
	public double getPtSize(Graphics2D g2d) {
		return (double) ptSize / g2d.getTransform().getScaleX();
	}

	/**
	 * Set the point size (diameter) in pixels
	 * @param s Size in pixels
	 */
	public void setPtSize(int s) {
		this.ptSize = s;
	}

	/**
	 * Paint the object.
	 * This sets the stroke to solid 1 pixel and sets the color 
	 * whether the object is visible or not
	 * @param g2d Graphics2D
	 */
	@Override
	public void paint(Graphics2D g2d) {	// This is further customized by extensions
		float scale = (float) g2d.getTransform().getScaleX();
		g2d.setStroke(new BasicStroke(1.0f / scale));
		g2d.setColor(color);
		if (visible) {
			g2d.draw(new Line2D.Double(pos, pos));
		}
	}

	/**
	 * Get the separation from this point to the specified point
	 * @param p point in inches
	 * @return distance in inches
	 */
	public double separation(Point2D.Double p) {
		return pos.distance(p);
	}
}
