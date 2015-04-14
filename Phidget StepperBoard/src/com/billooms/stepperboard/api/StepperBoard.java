
package com.billooms.stepperboard.api;

import com.phidgets.StepperPhidget;
import java.beans.PropertyChangeListener;

/**
 * StepperBoard is used to monitor the state of a Phidgets stepper board.
 * This is compatible with either a Phidgets 1063 or 1062 board. 
 * 1067 board is EXPERIMENTAL and untested.
 * Note: no checking is done to see if setting a new location is the same as the current location.
 * @author Bill Ooms Copyright (c) 2012 Studio of Bill Ooms all rights reserved
 */
public interface StepperBoard {
	/** Default acceleration is the max for 1062 board */
	double DEFAULT_ACCEL = 8859.375;
	/** Velocity resolution for 1062 and 1063 and 1067 */
	double[] VELOCITY_RES = {0.75, 8.0, 1.0};
	/** Acceleration resolution for 1062 and 1063 and 1067 */
	double[] ACCEL_RES = {140.625, 4,000.0, 1.0};
	/** Micro-stepping for 1062 and 1063 and 1067 */
	int[] MICRO_STEP = {2, 16, 16};
	/** Max current for 1063 is 2.5, but I'm using 2.0 because of power supply limits */
	double[] MAX_CURRENT = {1.0, 2.5, 4.0};
	
	/** 
	 * Names for the 4 steppers for 1062 board. 
	 * A 1063 board should only use S0 
	 */
	static enum Stepper {
		/** Stepper connected to terminals 0 */
		S0,
		/** Stepper connected to terminals 1 */
		S1,
		/** Stepper connected to terminals 2 */
		S2,
		/** Stepper connected to terminals 3 */
		S3
	}
	
	/** For property changes */
	static enum Props {
		/** PropertyName when an input changes */
		INPUT, 
		/** PropertyName when the steppers are engaged or disengaged */
		ENGAGED,
		/** PropertyName when a stepper's position is changed */
		POSITION,
		/** PropertyName when a stepper's target position is changed */
		TARGET,
		/** PropertyName when a stepper's velocity is changed */
		VELOCITY,
		/** PropertyName when the velocity limit is changed */
		VLIMIT,
		/** PropertyName when the acceleration limit is changed */
		ALIMIT,
		/** PropertyName when the current is changed */
		CURRENT,
		/** PropertyName when the current limit is changed */
		CLIMIT,
		/** PropertyName when a stepper is stopped */
		STOPPED,
		/** PropertyName when all steppers are stopped */
		ALL_STOPPED,
		/** PropertyName when the board is attached */
		ATTACH,
		/** PropertyName when the board is detached */
		DETACH
	}

    /**
     * Get the StepperPhidget object.
     * @return StepperPhidget
     */
	StepperPhidget getPhidget();
	
	/**
	 * Get the type of board.
	 * @return 0 is 1062, 1 is 1063
	 */
	int getType();
	
	/**
	 * Get the micro-step value for the connected board type;
	 * @return 1062 is 2; 1063 is 16
	 */
	int getMicroStep();
	
	/**
	 * Get the number of inputs.
	 * A 1062 board as 0 and a 1063 board has 4.
	 * @return number of inputs
	 */
	int getNInputs();
	
	/**
	 * Get the number of steppers supported by this board.
	 * A 1062 has 4 and a 1063 has 1.
	 * @return number of steppers
	 */
	int getNSteppers();

	/**
	 * Get an array of states of all inputs.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return input state array
	 */
	boolean[] getInStates();

    /**
     * Get the input state from the array (this is to save time).
     * @param in Input
     * @return The state of the input
     */
	boolean getInState(int in);

	/**
	 * Get an array of positions of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return position array
	 */
	long[] getPositions();

	/**
	 * Get an array of the engaged status of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return engaged array
	 */
	boolean[] getEngaged();

	/**
	 * Get an array of the stopped status of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return stopped array
	 */
	boolean[] getStopped();

	/**
	 * Get an array of velocities of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return velocity array
	 */
	double[] getVelocities();

	/**
	 * Get an array of velocity limits of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return velocity limit array
	 */
	double[] getVLimits();

	/**
	 * Get an array of acceleration limits of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return acceleration limit array
	 */
	double[] getALimits();

	/**
	 * Get an array of target positions of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return target array
	 */
	long[] getTargets();
	
	/**
	 * Get an array of motor currents.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return current array
	 */
	double[] getCurrents();
	
	/**
	 * Get an array of motor current limits.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return current limit array
	 */
	double[] getCLimits();

    /**
     * Check to see if the board is attached.
	 * The board is polled to determine the current status 
	 * (which takes longer than reading the table values). 
     * @return true = attached
     */
	boolean isAttached();

    /**
     * Get the state of a motor to see if it is engaged.
	 * Note that this is retrieved from an array of saved states to save time.
     * @param s Stepper
     * @return true=engaged, false=not engaged
     */
	boolean getEngaged(Stepper s);

    /**
     * Set the engaged state of a motor.
	 * An ENGAGED PropertyChangeEvent is fired with the old and new state. 
     * @param s Stepper
     * @param state true=engaged, false=disengaged
     */
	void setEngaged (Stepper s, boolean state);

    /**
     * Check to see if a motor is stopped and update the table.
	 * The board is polled to determine the current status. 
	 * A STOPPED PropertyChangeEvent is fired with the old and new state. 
     * @param s Stepper
     * @return true=stopped
     */
	boolean stopCheck(Stepper s);

    /**
     * Disengage all motors.
	 * An ENGAGED PropertyChangeEvent is fired (with null for old and 'false' for new). 
     */
	void disengageAll();

    /**
     * Get the maximum velocity allowed (all motors are the same).
     * @return maximum velocity allowed
     */
	double getMaxVelocity();

    /**
     * Get the minimum velocity allowed (all motors are the same).
	 * Note that this is not zero, but rather the minimum resolution.
     * @return minimum velocity allowed
     */
	double getMinVelocity();

    /**
     * Get the maximum acceleration allowed (all motors are the same).
     * @return maximum acceleration allowed
     */
	double getMaxAcceleration();

    /**
     * Get the minimum acceleration allowed (all motors are the same).
	 * Note that this is not zero, but rather the minimum resolution.
     * @return minimum acceleration allowed
     */
	double getMinAcceleration();

    /**
     * Set the velocity limit of a motor.
	 * Note: this will limited by min resolution and max limit.
	 * A VLIMIT PropertyChangeEvent is fired with the old and new values. 
     * @param s Stepper
     * @param val Velocity limit
     */
	void setVelocityLimit(Stepper s, double val);
	
	/**
	 * Get the acceleration setting of the motor
     * @param s Stepper
	 * @return acceleration
	 */
	double getAcceleration(Stepper s);

    /**
     * Set the acceleration of a motor.
	 * Note: this will limited by min resolution and max limit.
	 * A ALIMIT PropertyChangeEvent is fired with the old and new values. 
     * @param s Stepper
     * @param val acceleration
     */
	void setAcceleration(Stepper s, double val);

	/**
	 * Get the previously set velocity limit of a motor.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @param s Stepper
	 * @return velocity limit
	 */
	double getVelocityLimit(Stepper s);

    /**
     * Set the motor velocity limit to the maximum.
	 * A VLIMIT PropertyChangeEvent is fired with the old and new values. 
     * @param s Stepper
     */
    void setVelocityToMax(Stepper s);

    /**
     * Get the current position from the board.
	 * The board is polled to determine the current position. 
	 * (which takes longer than reading the table values).
     * @param s Stepper
     * @return Current position
     */
	long getCurrentPosition(Stepper s);

    /**
     * Get the current position from the array.
	 * Note that this is retrieved from an array of saved states to save time,
	 * and doesn't run the risk of generating another stopped motor indication.
     * @param s Stepper
     * @return Current position
     */
	long getPosition(Stepper s);

    /**
     * Set the current position of a motor.
	 * A POSITION PropertyChangeEvent is fired with the old and new values.
     * @param s Stepper
     * @param val New current position
     */
	void setCurrentPosition(Stepper s, int val);

    /**
     * Get the target position from the board.
	 * The board is polled to determine the current position. 
	 * (which takes longer than reading the table values).
     * @param s Stepper
     * @return Target position
     */
	long getTargetPosition(Stepper s);

    /**
     * Set the target position of a motor.
	 * A TARGET PropertyChangeEvent is fired with the old and new values.
	 * Also, if the new target position is different than the current position,
	 * a STOPPED PropertyChangeEvent is fired with the old state and 'false' for the new.
     * @param s Stepper
     * @param val Target position
     */
	void setTargetPosition(Stepper s, long val);
	
	/**
	 * Get the current limit of a motor.
	 * The board is polled to determine the current position. 
	 * (which takes longer than reading the table values).
     * @param s Stepper
	 * @return current limit
	 */
	double getCurrentLimit(Stepper s);
	
	/**
	 * Set the current limit of a motor.
	 * Note: this will limited by the max limit.
	 * A CLIMIT PropertyChangeEvent is fired with the old and new values.
     * @param s Stepper
     * @param cur Current limit
	 */
	void setCurrentLimit(Stepper s, double cur);

	/**
	 * Disengage all steppers and close the Phidget.
	 */
	void close();

	/**
	 * Add a PropertyChangeListener.
	 * @param listener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener.
	 * @param listener
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
	
}
