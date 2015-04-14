
package com.billooms.indexerprefs;

import com.billooms.indexerprefs.api.Preferences;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 * Interface for accessing the hardware preferences
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
@ServiceProvider(service = Preferences.class)
public class PreferencesImpl implements Preferences {
	
	private int stepper;	// Stepper motor steps per rotation
	private int micro;		// Driver micro-stepping (2 = 1/2 microstepping)
	private int small;		// Number of teeth on small pulley (on motor)
	private int large;		// Number of teeth on large pulley (on spindle)
	private int stepsPerRotation;	// stepper microsteps per spindle rotation
	private int wiredTo;	// position on the board that the stepper is wired to 
	private double current;	// current limit
	private double accel;	// acceleration micro-pulse/sec^2
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public PreferencesImpl() {
		update();
	}
	
	/**
	 * Get the number of micro-steps per revolution of the spindle.
	 * This is a function of the stepper, the driver, and the pulleys (gears).
	 * @return micro-steps per revolution
	 */
	@Override
	public int getStepsPerRotation() {
		return stepsPerRotation;
	}

	/**
	 * Get the position on the board that the spindle stepper is wired to.
	 * @return position for spindle stepper
	 */
	@Override
	public int getWiredTo() {
		return wiredTo;
	}

	@Override
	public int getMicro() {
		return micro;
	}

	@Override
	public double getCurrentLimit() {
		return current;
	}

	@Override
	public double getAccel() {
		return accel;
	}
	
	/**
	 * Update all values from the last saved preferences
	 */
	@Override
	public void update() {
		switch(NbPreferences.forModule(PreferencesPanel.class).getInt("stepper", 0)) {
			default:
			case 0:
				stepper = 200;
				break;
			case 1:
				stepper = 400;
				break;
		}
		micro = NbPreferences.forModule(PreferencesPanel.class).getInt("microStep", 2);
		small = NbPreferences.forModule(PreferencesPanel.class).getInt("smallGear", 20);
		large = NbPreferences.forModule(PreferencesPanel.class).getInt("largeGear", 130);
		int oldSteps = stepsPerRotation;
		stepsPerRotation = stepper * micro * large / small;
		pcs.firePropertyChange(PROP_STEPSPERROTATION, oldSteps, stepsPerRotation);
		int oldWired = wiredTo;
		wiredTo = NbPreferences.forModule(PreferencesPanel.class).getInt("wired", 0);
		pcs.firePropertyChange(PROP_WIRETO, oldWired, wiredTo);
		double oldc = current;
		current = NbPreferences.forModule(PreferencesPanel.class).getDouble("current", 0.0);
		pcs.firePropertyChange(PROP_CURRENT, oldc, current);
		double oldacc = accel;
		accel = NbPreferences.forModule(PreferencesPanel.class).getDouble("accel", 10.0) * stepsPerRotation / (2.0 * Math.PI);
		pcs.firePropertyChange(PROP_ACCEL, oldacc, accel);
	}

	/**
	 * Add the given PropertyChangeListener to this object
	 * @param listener
	 */
	@Override
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove the given PropertyChangeListener from this object
	 * @param listener
	 */
	@Override
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
}
