package com.billooms.indexwheel.api;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;

/**
 * Implementation of an index wheel on a lathe.
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public interface IndexWheel {
	/** The radius of the IndexWheel for drawing purposes */
	double WHEEL_RADIUS = 3.0;
	/** Property name used for changes in this object's name */
	String PROP_NAME = "name";
	/** Property name used for changing the number of holes */
	String PROP_NUMHOLES = "numHoles";
	/** Property name used for changing the phase */
	String PROP_PHASE = "phase";
	/** Property name used for changing the rotation */
	String PROP_ROTATION = "rotation";
	/** Property name used for clearing all the holes */
	String PROP_CLEARALL = "clearAll";
	/** Property name used for filline all the holes */
	String PROP_FILLALL = "fillAll";
	/** Property name used for filling a hole */
	String PROP_FILLHOLE = "fillHole";
	/** Property name used for toggling the "fill" state of a hole */
	String PROP_TOGGLEFILL = "toggleFill";

    /**
     * Paint the object.
     * @param g2d Graphics2D
     */
    void paint(Graphics2D g2d);
	
	/**
	 * Get this object's unique ID which does not change.
	 * @return unique id
	 */
	String getID();

	/**
	 * Get the string name
	 * @return name
	 */
	String getName();

	/**
	 * Set the string name. 
	 * This fires a PROP_NAME property change with the old and new names. 
	 * @param str name
	 */
	void setName(String str);

	/**
	 * Get the number of holes in the wheel.
	 * @return total number of holes.
	 */
	int getNumHoles();

	/**
	 * Set the number of holes in the wheel. 
	 * Values less than 1 will be interpreted as 1. 
	 * This fires a PROP_NUMHOLES property change with the old and new values. 
	 * Note: this makes all holes un-filled.
	 * @param n number of holes
	 */
	void setNumHoles(int n);

	/**
	 * Get the phase shift of the wheel.
	 * @return phase in fraction of the hole spacing (range is 0.0 to 1.0)
	 */
	double getPhase();

	/**
	 * Set the phase of the wheel. 
	 * This fires a PROP_PHASE property change with the old and new values. 
	 * @param ph phase in fraction of the hole spacing (range is 0.0 to 1.0)
	 */
	void setPhase(double ph);

	/**
	 * Get the current rotation of the wheels in degrees. 
	 * Positive rotation is counter-clockwise.
	 * Note that all wheels share a common rotation. 
	 * @return current rotation in degrees
	 */
	double getRotation();

	/**
	 * Set the current rotation of the wheels to the given value. 
	 * Positive rotation is counter-clockwise.
	 * Note that all wheels share a common rotation as if they were all 
	 * connected together on a single shaft.
	 * This fires a PROP_ROTATION property change with the old and new values. 
	 * @param r new rotation in degrees
	 */
	void setRotation(double r);

	/**
	 * Get the rotation of the next filled hole. 
	 * The rotation might wrap around from the largest number back to zero, 
	 * so the number might be more than 360.0 degrees.
	 * Positive rotation is counter-clockwise.
	 * Note that this does not change the rotation of the wheel. 
	 * It only gets the rotation of the next filled hole. 
	 * Use setRotation(r) to change the rotation. 
	 * @param res The resolution of the spindle in micro-steps per revolution.
	 * @return the rotation of the next filled hole in degrees. 
	 * This is a positive number representing the absolute rotation (not incremental). 
	 * Returns the current rotation if there are no filled holes.
	 */
	double getRotationOfNext(int res);

	/**
	 * Clear all the holes (i.e. not filled). 
	 * This fires a PROP_CLEARALL property change. 
	 */
	void clearAll();

	/**
	 * Fill all the holes.
	 * This fires a PROP_FILLALL property change. 
	 */
	void fillAll();

	/**
	 * Fill or clear a specific hole. 
	 * This fires a PROP_FILLHOLE property change with the number of the hole filled. 
	 * Note: negative numbers or values >= numHoles will be ignored
	 * @param n hole to fill
	 * @param f true=fill, false=clear
	 */
	void fillHole(int n, boolean f);

	/**
	 * Toggle the fill of the hole nearest the given point (within given distance).
	 * This fires a PROP_TOGGLEFILL property change with the number of the hole. 
	 * @param p Point in pixels (screen coordinates)
	 * @param dis distance to measure
	 * @return true=hole was found and changed, false=nothing changed
	 */
	boolean toggleHoleNearest(Point2D.Double p, double dis);

	/**
	 * Write IndexWheel information to an xml file.
	 * @param out output stream for writing
	 */
	void writeXML(PrintWriter out);

	/**
	 * Add the given PropertyChangeListener to this object.
	 * @param listener
	 */
    void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove the given PropertyChangeListener from this object.
	 * @param listener
	 */
    void removePropertyChangeListener(PropertyChangeListener listener);
}
