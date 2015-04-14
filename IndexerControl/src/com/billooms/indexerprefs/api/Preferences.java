
package com.billooms.indexerprefs.api;

import java.beans.PropertyChangeListener;

/**
 * Interface for accessing the hardware preferences
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public interface Preferences {
	/** All IndexerPref property change names start with this prefix */
	String PROP_PREFIX = "Pref";
	
	String PROP_WIRETO = PROP_PREFIX + "wiredTo";
	String PROP_STEPSPERROTATION = PROP_PREFIX + "stepsPerRotation";
	String PROP_CURRENT = PROP_PREFIX + "Current";
	String PROP_ACCEL = PROP_PREFIX + "Acceleration";
	
	/**
	 * Get the number of micro-steps per revolution of the spindle.
	 * This is a function of the stepper, the driver, and the pulleys (gears).
	 * @return micro-steps per revolution
	 */
	int getStepsPerRotation();
	
	/**
	 * Get the position on the board that the spindle stepper is wired to.
	 * @return position for spindle stepper
	 */
	int getWiredTo();
	
	/**
	 * Get the micro-stepping value
	 * @return micro-steps
	 */
	int getMicro();
	
	/**
	 * Get the current limit.
	 * @return current limit
	 */
	double getCurrentLimit();
	
	/**
	 * Get the acceleration in microsteps per second^2
	 * @return acceleration
	 */
	double getAccel();
	
	/**
	 * Update all values from the last saved preferences
	 */
	void update();

	/**
	 * Add the given PropertyChangeListener to this object
	 * @param listener
	 */
    void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove the given PropertyChangeListener from this object
	 * @param listener
	 */
    void removePropertyChangeListener(PropertyChangeListener listener);
}
