
package com.billooms.indexercontrol;

import com.billooms.indexerprefs.api.Preferences;
import com.billooms.stepperboard.api.StepperBoard;
import com.billooms.stepperboard.api.StepperBoard.Stepper;
import org.openide.util.Lookup;

/**
 * This controls a stepper motor on a stage
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
public class Stage {
	
	protected StepperBoard stepBoard;
	protected Stepper stepper;

    /**
     * This controls a stepper motor on a stage
     */
	public Stage(Stepper s) {
		this.stepper = s;
		stepBoard = Lookup.getDefault().lookup(StepperBoard.class);
//		stepBoard.setAcceleration(stepper, Lookup.getDefault().lookup(Preferences.class).getAccel());
//		if (stepBoard.getType() == 1) {
//			stepBoard.setCurrentLimit(stepper, Lookup.getDefault().lookup(Preferences.class).getCurrentLimit());
//		}
	}   // end constructor

	/**
	 * Get the maximum possible velocity of the stepper
	 * @return max velocity in half-steps/second
	 */
	public double getMaxVelocity() {
		return stepBoard.getMaxVelocity();
	}

	/**
	 * Get the minimum possible velocity of the stepper
	 * @return min velocity in half-steps/second
	 */
	public double getMinVelocity() {
		return stepBoard.getMinVelocity();
	}

	/**
	 * Get the velocity limit of the stage motor.
	 * Note: this comes from the table to save time
	 * @return velocity limit
	 */
	public double getVelocityLimit() {
		return stepBoard.getVelocityLimit(stepper);
	}

    /**
     * Set the velocity limit of the stage motor.
	 * If the new value is the same as the old value, no command will be issued.
     * @param val New velocity limit
	 * @return true = value was changed, false = value was the same, no command was issued
	 * 
     */
	public boolean setVelocityLimit(double val) {
		if (getVelocityLimit() == val) {
			return false;
		}
		stepBoard.setVelocityLimit(stepper, val);
		return true;
	}

    /**
     * Set the stage motor velocity limit to the maximum.
	 * If the new value is the same as the old value, no command will be issued.
	 * @return true = value was changed, false = value was the same, no command was issued
     */
    public boolean setVelocityToMax() {
		return setVelocityLimit(getMaxVelocity());
    }

	/**
	 * Get the position in steps (from the table)
	 * @return position in steps
	 */
	public long getPositionSteps() {
		return stepBoard.getPosition(stepper);
	}

    /**
     * Check if this stage is engaged
     * @return true=engaged
     */
	public boolean getEngaged() {
		return stepBoard.getEngaged(stepper);
	}

    /**
     * Engage (or disengage) this stage
     * @param state true=engage, false=disengage
	 * @return true=OK, false=problem (like the board is not attached)
     */
	public boolean setEngaged(boolean state) {
		if (!stepBoard.isAttached()) {
			return false;
		}
		if (state) {
			stepBoard.setCurrentPosition(stepper, 0);	// Make sure stepper isn't going to move further
			stepBoard.setTargetPosition(stepper, 0);
			stepBoard.setAcceleration(stepper, Lookup.getDefault().lookup(Preferences.class).getAccel());
			if (stepBoard.getType() == 1) {
				stepBoard.setCurrentLimit(stepper, Lookup.getDefault().lookup(Preferences.class).getCurrentLimit());
			}
		}
		stepBoard.setEngaged(stepper, state);
		return true;
	}

    /**
     * Set the current location to the new zero reference.
     */
    public void setZero() {
		if (!stepBoard.isAttached()) {
			return;
		}
        this.stop();        // make sure we're stopped firse
        stepBoard.setCurrentPosition(stepper, 0);
        stepBoard.setTargetPosition(stepper, 0);
    }

    /**
     * Stop the stage by making reading current position and making it the target position
     */
	public void stop() {
		stepBoard.setTargetPosition(stepper, stepBoard.getCurrentPosition(stepper));	// Get the current position from the board (not the table)
        stepBoard.stopCheck(stepper);		// make sure the table is updated
	}

    /**
     * Shut down everything.
	 * Note: In an installation with more than one stepper, 
	 * this should be in the class controlling all stages!
     */
	public void shutDown() {
        System.out.println("Stage.shutDown");
		if (stepBoard.isAttached()) {
			stepBoard.disengageAll();
			while (getEngaged()) {
				// Wait for completion of disengage before exiting
			}
			stepBoard.close();
		}
	}
}
