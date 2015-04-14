package com.billooms.indexwheel.drawables;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Drawable text defined by inch(cm) location
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public class Text extends Pt {

	/**
	 * Justification for the text
	 */
	public enum Justify {
		/**
		 * Left justify
		 */
		LEFT,
		/**
		 * Center justify
		 */
		CENTER,
		/**
		 * Right justify
		 */
		RIGHT
	};
	
	private final static Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);
	
	private String str;
	private Justify justify = Justify.CENTER;
	private Font font = DEFAULT_FONT;

	/**
	 * Drawable text defined by inch location and color
	 * @param pos text location in inches
	 * @param s String
	 * @param c Color
	 */
	public Text(Point2D.Double pos, String s, Color c) {
		super(pos, c);
		this.str = s;
	}

	/**
	 * Drawable text defined by inch location (default color)
	 * @param pos text location in inches
	 * @param s String
	 */
	public Text(Point2D.Double pos, String s) {
		super(pos);
		this.str = s;
	}

	/**
	 * Get the text string
	 * @return text string
	 */
	public String getText() {
		return str;
	}

	/**
	 * Set the text to a new string
	 * @param s text string
	 */
	public void setText(String s) {
		this.str = s;
	}

	/**
	 * Get the justification of the text
	 * @return one of LEFT, CENTER, RIGHT
	 */
	public Justify getJustify() {
		return justify;
	}

	/**
	 * Set the justification for the text
	 * @param j one of LEFT, CENTER, RIGHT
	 */
	public void setJustify(Justify j) {
		this.justify = j;
	}

	/**
	 * Get the font used for this text
	 * @return font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Set the font for this text
	 * @param f font
	 */
	public void setFont(Font f) {
		this.font = f;
	}

	/**
	 * Paint the object
	 * Note: this works in pixels because else the text is REALLY BIG!
	 * @param g2d Graphics2D
	 */
	@Override
	public void paint(Graphics2D g2d) {
		super.paint(g2d);	// sets color and stroke
		if (visible) {
			AffineTransform saveXform = g2d.getTransform();	// save for later restoration
			g2d.setTransform(new AffineTransform());		// work directly in pixels

			Point pix = scaleInchToPix(saveXform);			// location of text in pixels

			g2d.setFont(font);
			FontMetrics fm = g2d.getFontMetrics(font);
			switch (justify) {
				case CENTER:
					g2d.drawString(str, pix.x - fm.stringWidth(str) / 2, pix.y + fm.getAscent() / 2);
					break;
				case RIGHT:
					g2d.drawString(str, pix.x - fm.stringWidth(str), pix.y + fm.getAscent() / 2);
					break;
				case LEFT:
					g2d.drawString(str, pix.x, pix.y + fm.getAscent() / 2);
					break;
			}

			g2d.setTransform(saveXform);					// Restore transform
		}
	}

	/**
	 * Determine the location of the object in pixels for a given transform
	 * @param xform Transform
	 * @return location in pixels
	 */
	private Point scaleInchToPix(AffineTransform xform) {
		double dpi = xform.getScaleX();
		double zeroX = xform.getTranslateX();
		double zeroY = xform.getTranslateY();
		return new Point((int) zeroX + (int) (pos.x * dpi), (int) zeroY - (int) (pos.y * dpi));
	}
}