
package com.billooms.indexercontrol;

import com.billooms.indexerprefs.api.Preferences;
import com.billooms.stepperboard.api.StepperBoard.Stepper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.util.Lookup;

/**
 * This controls a stepper motor rotating a spindle (no limit switches)
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
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
public class RotationStage extends Stage implements PropertyChangeListener {
	
	private static int stepsPerRotation;

    /**
     * This controls a stepper motor on a stage
     */
	public RotationStage(Stepper s) {
		super(s);
		
		stepsPerRotation = Lookup.getDefault().lookup(Preferences.class).getStepsPerRotation();
	}	// end constructor

	/**
	 * Get the maximum rpm of the spindle
	 * @return maximum rpm
	 */
	public double getMaxRPM() {
		return 60.0 * stepBoard.getMaxVelocity()/stepsPerRotation;	//  60.0 seconds/minute
	}

	/**
	 * Set the stepper velocity to a given spindle rpm.
	 * If rpm > max, then set to max.
	 * @param rpm rotations per minute
	 * @return true = value was changed, false = value was the same, no command was issued
	 */
	public boolean setSpindleRPM(double rpm) {
		return setVelocityLimit(rpmToVelocity(rpm));
	}

    /**
     * Get the current position in degrees
     * @return position in degrees
     */
    public double getPosition() {
        return stepToDegree(stepBoard.getPosition(stepper));
    }

	/**
	 * Set the current position in degrees
	 * @param x new value for the current position
	 */
	public void setPosition(double d) {
		stepBoard.setCurrentPosition(stepper, (int)degreeToStep(d));
	}

	/**
	 * Check to see if the given degree value will be the same integer
	 * as the current stepper position
	 * @param deg given position in degrees
	 * @return true=no movement, false=there will be some movement
	 */
	public boolean isPosition(double deg) {
		if (degreeToStep(deg) == stepBoard.getPosition(stepper)) {
			return true;
		}
		return false;
	}

	/**
	 * Make sure that theta is between -180 and +180 degrees
	 * by adding/subtraction 360 to the position.
	 * @return true = changed, false = not changed
	 */
	public boolean wrapAroundCheck() {
		boolean changed = false;
		if (!stepBoard.stopCheck(stepper)) {
			System.out.println("Warning: WrapAround Check while stepper was not stopped! " +
								stepBoard.getPosition(stepper));
		}
		long lastPosition = stepBoard.getCurrentPosition(stepper);	// Get the current position from the board (not the table)
		while (lastPosition > stepsPerRotation/2) {
			lastPosition = lastPosition - stepsPerRotation;
			stepBoard.setCurrentPosition(stepper, (int)lastPosition);
			stepBoard.setTargetPosition(stepper, (int)lastPosition);
			changed = true;
		}
		while (lastPosition < -stepsPerRotation/2) {
			lastPosition = lastPosition + stepsPerRotation;
			stepBoard.setCurrentPosition(stepper, (int)lastPosition);
			stepBoard.setTargetPosition(stepper, (int)lastPosition);
			changed = true;
		}
		return changed;
	}

	/**
     * Go to a given position (in degrees) at specified velocity.
	 * Always go even if new position is the same as last position.
	 * This is useful for joystick control.
     * @param deg Position in degrees
	 * @param vel Velocity
	 */
	public void alwaysGoToAtVelocity(double deg, double vel) {
		if (getEngaged()) {
			setVelocityLimit(vel);
			stepBoard.setTargetPosition(stepper, degreeToStep(deg));
		}
	}

	/**
     * Go to a given position (in degrees) at specified velocity.
	 * Take no action if new position is the same as last position.
     * @param deg Position in degrees
	 * @param vel Velocity
	 * @return true = position was changed, false = position was the same,
	 * no commands were issued, and velocity limit was not changed.
	 */
	public boolean goToAtVelocity(double deg, double vel) {
		if (getEngaged()) {
			long newPosition = degreeToStep(deg);
			if (newPosition != stepBoard.getPosition(stepper)) {	// Don't try moving if no change
																	// (to avoid a momentary "Motor Stopped" indication)
				if (vel <= 0.0) {
					System.out.println("Warning: Attempt to move to " + deg +
									   " on stepper "+ stepper + " with zero velocity!");
					return false;
				}
				setVelocityLimit(vel);
				stepBoard.setTargetPosition(stepper, newPosition);
				return true;
			}
		}
		return false;
	}

    /**
     * Go to a given position (in degrees) at maximum velocity.
	 * Take no action if new position is the same as last position.
     * @param deg Position in degrees
	 * @return true = position was changed, false = position was the same,
	 * no commands were issued, and velocity limit was not changed.
     */
	public boolean goToAtMax(double deg) {
		return goToAtVelocity(deg, getMaxVelocity());
	}

	/**
	 * Go to a given position (in degrees) at the previously specified rpm.
	 * Take no action if new position is the same as last position.
	 * @param deg
	 * @param rpm
	 * @return true = position was changed, false = position was the same,
	 * no commands were issued, and velocity limit was not changed.
	 */
	public boolean goToAtRPM(double deg, double rpm) {
		return goToAtVelocity(deg, rpmToVelocity(rpm));
	}

	/**
	 * Convert from rpm to velocity
	 * @param rpm rotations per minute
	 * @return steps per second
	 */
	public double rpmToVelocity(double rpm) {
		return getMaxVelocity() * Math.min(rpm/getMaxRPM(), 1.0);
	}

    /**
     * Convert degrees to steps
     * @param deg degrees
     * @return steps
     */
	public static long degreeToStep(double deg) {
		return Math.round(deg*stepsPerRotation/360.0);
	}

    /**
     * Convert from steps to degrees
     * @param s steps
     * @return degree
     */
	public static double stepToDegree(long s) {
		return 360.0 * (double)s / (double)(stepsPerRotation);
	}

	/**
	 * Listen for changes in preferences that effect how the stepper is wired 
	 * or the number of stepper micro-steps per rotation.
	 * @param evt 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Preferences.PROP_STEPSPERROTATION)) {
			stepsPerRotation = Lookup.getDefault().lookup(Preferences.class).getStepsPerRotation();
		} else if (evt.getPropertyName().equals(Preferences.PROP_WIRETO)) {
			stepper = Stepper.values()[Lookup.getDefault().lookup(Preferences.class).getWiredTo()];
		} else if (evt.getPropertyName().equals(Preferences.PROP_CURRENT)) {
			stepBoard.setCurrentLimit(stepper, Lookup.getDefault().lookup(Preferences.class).getCurrentLimit());
		} else if (evt.getPropertyName().equals(Preferences.PROP_ACCEL)) {
			stepBoard.setAcceleration(stepper, Lookup.getDefault().lookup(Preferences.class).getAccel());
		}
	}
}
