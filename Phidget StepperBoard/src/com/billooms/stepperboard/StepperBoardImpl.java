
package com.billooms.stepperboard;

import com.billooms.stepperboard.api.StepperBoard;
import com.billooms.stepperboard.api.StepperBoard.Props;
import com.billooms.stepperboard.api.StepperBoard.Stepper;
import com.phidgets.PhidgetException;
import com.phidgets.StepperPhidget;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.CurrentChangeEvent;
import com.phidgets.event.CurrentChangeListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.InputChangeEvent;
import com.phidgets.event.InputChangeListener;
import com.phidgets.event.StepperPositionChangeEvent;
import com.phidgets.event.StepperPositionChangeListener;
import com.phidgets.event.StepperVelocityChangeEvent;
import com.phidgets.event.StepperVelocityChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.lookup.ServiceProvider;

/**
 * StepperBoard is used to monitor the state of a Phidgets stepper board.
 * This is specifically written for a Phidgets 1062 and 1063 boards.
 * Note: no checking is done to see if setting a new location is the same as the current location.
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ServiceProvider(service = StepperBoard.class)
public class StepperBoardImpl implements StepperBoard {

	// Saved States from ChangeListeners
	private final static int MAXSTEPPERS = 4;
	private final static int MAXINPUTS = 4;
	private boolean inputState[] = new boolean[MAXINPUTS];
	private boolean engaged[] = new boolean[MAXSTEPPERS];
	private boolean stopped[] = new boolean[MAXSTEPPERS];
	private long position[] = new long[MAXSTEPPERS];
	private long target[] = new long[MAXSTEPPERS];
	private double velocity[] = new double[MAXSTEPPERS];
	private double vLimit[] = new double[MAXSTEPPERS];
	private double aLimit[] = new double[MAXSTEPPERS];
	private double[] current = new double[MAXSTEPPERS];
	private double[] cLimit = new double[MAXSTEPPERS];
	
	private int nSteppers;
	private int nInputs;
	private int type;			// 0 is 1062; 1 is 1063; 2 is 1067
	private StepperPhidget stepPhidget;
	private double maxVelocity, minVelocity;
	private double maxAccel, minAccel;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * Create a new StepperBoard.
	 * Various change listeners are attached to monitor the status of the board.
	 * An ATTACH PropertyChangeEvent is fired when the board is attached.
	 * A DETACH PropertyChangeEvent is fired when the board is detached.
	 * A POSITION PropertyChangeEvent is fired when the position of a stepper is changed, 
	 * with the old and new positions.
	 * A STOPPED PropertyChangeEvent is fired when a stepper starts or stops, 
	 * with the old and new states.
	 * A VELOCITY PropertyChangeEvent is fired when the velocity of a stepper is changed, 
	 * with the old and new values.
	 */
    public StepperBoardImpl() {
		try {
			stepPhidget = new StepperPhidget();
			stepPhidget.addAttachListener(new AttachListener() {
				@Override
				public void attached(AttachEvent evt) {
					try {
						nSteppers = stepPhidget.getMotorCount();
						if (nSteppers == 4) {	// determine type of board
							type = 0;
						} else {
							type = 1;
						}
						if (stepPhidget.getDeviceVersion() >= 200) {
							type = 2;		// must be a 1067 board
						}
						maxVelocity = stepPhidget.getVelocityMax(0);
						minVelocity = Math.max(stepPhidget.getVelocityMin(0), VELOCITY_RES[type]);
						maxAccel = stepPhidget.getAccelerationMax(0);
						minAccel = Math.max(stepPhidget.getAccelerationMin(0), ACCEL_RES[type]);
						
						for (int i = 0; i < nSteppers; i++) {
							aLimit[i] = DEFAULT_ACCEL;
							stepPhidget.setAcceleration(i, DEFAULT_ACCEL);	// set default acceleration
							vLimit[i] = maxVelocity;
							stepPhidget.setVelocityLimit(i, maxVelocity);	// default limit is max
						}
						for (int i = 0; i < nInputs; i++) {
							inputState[i] = stepPhidget.getInputState(i);
						}
						pcs.firePropertyChange(Props.ATTACH.toString(), null, null);
						String boardType;
						switch (type) {
							case 0:
								boardType = "1062";
								break;
							case 1:
								boardType = "1063";
								break;
							case 2:
								boardType = "1067";
								break;
							default:
								boardType = "unknown";
								break;
						}
						System.out.println("Phidget board attached: " + boardType);
						System.out.println("  deviceLabel:" + stepPhidget.getDeviceLabel());
						System.out.println("  deviceVersion:" + stepPhidget.getDeviceVersion());
						System.out.println("  deviceClass:" + stepPhidget.getDeviceClass());
						System.out.println("  deviceName:" + stepPhidget.getDeviceName());
						System.out.println("  deviceType:" + stepPhidget.getDeviceType());
					} catch (PhidgetException ex) {
						showError("StepperBoard.attached", ex);
					}
				}
			});
			stepPhidget.addDetachListener(new DetachListener() {
				@Override
				public void detached(DetachEvent evt) {
					pcs.firePropertyChange(Props.DETACH.toString(), null, null);
					System.out.println("StepperBoard detached");
				}
			});
            stepPhidget.addErrorListener(new ErrorListener() {
				@Override
                public void error(ErrorEvent evt) {
					NotifyDescriptor d = new NotifyDescriptor.Message("StepperBoard.addErrorListener\n" + evt.toString(), NotifyDescriptor.ERROR_MESSAGE);
					DialogDisplayer.getDefault().notify(d);
                }
            });
			stepPhidget.addStepperPositionChangeListener(new StepperPositionChangeListener() {
				@Override
				public void stepperPositionChanged(StepperPositionChangeEvent evt) {
					int n = evt.getIndex();
					long val = (long)evt.getValue();
					long old = position[n];
					position[n] = val;
					pcs.firePropertyChange(Props.POSITION.toString(), old, val);
					try {
						boolean isStopped = stepPhidget.getStopped(n);
//						if (stopped) {
//							System.out.println("    Stepperboard: " + Step.values()[n] + " is stopped");
//						}
						boolean oldStop = stopped[n];
						stopped[n] = isStopped;
						pcs.firePropertyChange(Props.STOPPED.toString(), oldStop, isStopped);
					} catch (PhidgetException ex) {
						showError("StepperBoard.stepperPositionChanged", ex);
					}
					updateMoving();
				}
			});
			stepPhidget.addStepperVelocityChangeListener(new StepperVelocityChangeListener() {
				@Override
				public void stepperVelocityChanged(StepperVelocityChangeEvent evt) {
					int n = evt.getIndex();
					double old = velocity[n];
					velocity[n] = evt.getValue();
					pcs.firePropertyChange(Props.VELOCITY.toString(), old, evt.getValue());
				}
			});
			stepPhidget.addCurrentChangeListener(new CurrentChangeListener() {
				@Override
				public void currentChanged(CurrentChangeEvent evt) {
					int n = evt.getIndex();
					double old = current[n];
					current[n] = evt.getValue();
					pcs.firePropertyChange(Props.CURRENT.toString(), old, evt.getValue());
				}
			});
            stepPhidget.addInputChangeListener(new InputChangeListener() {
				@Override
                public void inputChanged(InputChangeEvent evt) {
					boolean old = inputState[evt.getIndex()];
					inputState[evt.getIndex()] = evt.getState();	// save state in array
					pcs.firePropertyChange(Props.INPUT.toString(), old, evt.getState());
                }
            });

			stepPhidget.openAny();
		} catch (PhidgetException ex) {
			showError("StepperBoard.constructor", ex);
		}
    }

    /**
     * Check to see if all motors are stopped.
	 * An ALL_STOPPED PropertyChangeEvent is fired when all are stopped. 
     */
	private void updateMoving() {
		boolean moving = false;
		for (int i = 0; i < nSteppers; i++) {
			if (engaged[i] && !stopped[i]) {
				moving = true;
				break;
			}
		}
		if (!moving) {
//			System.out.println("    Stepperboard: all stopped");
			this.pcs.firePropertyChange(Props.ALL_STOPPED.toString(), null, null);	// let someone know that all motors have stopped
		}
	}

    /**
     * Get the StepperPhidget object.
     * @return StepperPhidget
     */
	@Override
	public StepperPhidget getPhidget() {
		return stepPhidget;
	}
	
	/**
	 * Get the type of board.
	 * @return 0 is 1062, 1 is 1063, 2 is 1063
	 */
	@Override
	public int getType() {
		return type;
	}
	
	/**
	 * Get the micro-step value for the connected board type;
	 * @return 1062 is 2; 1063 is 16; 1067 is 16
	 */
	@Override
	public int getMicroStep() {
		return MICRO_STEP[type];
	}
	
	/**
	 * Get the number of inputs.
	 * A 1062 board as 0 and a 1063 board has 4, 1067 has 0
	 * @return number of inputs
	 */
	@Override
	public int getNInputs() {
		return nInputs;
	}
	
	/**
	 * Get the number of steppers supported by this board.
	 * A 1062 has 4 and a 1063 has 1, 1067 has 1
	 * @return 
	 */
	@Override
	public int getNSteppers() {
		return nSteppers;
	}

	/**
	 * Get an array of states of all inputs.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return input state array
	 */
	@Override
	public boolean[] getInStates() {
		return inputState;
	}

    /**
     * Get the input state from the array (this is to save time).
     * @param in Input
     * @return The state of the input
     */
	@Override
	public boolean getInState(int in) {
		return inputState[in];	// Assumes the array has the correct state (its faster)
	}

	/**
	 * Get an array of positions of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return position array
	 */
	@Override
	public long[] getPositions() {
		return position;
	}

	/**
	 * Get an array of the engaged status of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return engaged array
	 */
	@Override
	public boolean[] getEngaged() {
		return engaged;
	}

	/**
	 * Get an array of the stopped status of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return stopped array
	 */
	@Override
	public boolean[] getStopped() {
		return stopped;
	}

	/**
	 * Get an array of velocities of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return velocity array
	 */
	@Override
	public double[] getVelocities() {
		return velocity;
	}

	/**
	 * Get an array of velocity limits of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return velocity limit array
	 */
	@Override
	public double[] getVLimits() {
		return vLimit;
	}

	/**
	 * Get an array of acceleration limits of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return acceleration limit array
	 */
	@Override
	public double[] getALimits() {
		return aLimit;
	}

	/**
	 * Get an array of target positions of all motors.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return target array
	 */
	@Override
	public long[] getTargets() {
		return target;
	}
	
	/**
	 * Get an array of motor currents.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return current array
	 */
	@Override
	public double[] getCurrents() {
		return current;
	}
	
	/**
	 * Get an array of motor current limits.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @return current limit array
	 */
	@Override
	public double[] getCLimits() {
		return cLimit;
	}

    /**
     * Check to see if the board is attached.
	 * The board is polled to determine the current status 
	 * (which takes longer than reading the table values). 
     * @return true = attached
     */
	@Override
	public boolean isAttached() {
		boolean att = false;
		try {
			att= stepPhidget.isAttached();
		} catch (PhidgetException ex) {
			showError("StepperBoard.isAttached", ex);
		}
		return att;
	}

    /**
     * Get the state of a motor to see if it is engaged.
	 * Note that this is retrieved from an array of saved states to save time.
     * @param s Stepper
     * @return true=engaged, false=not engaged
     */
	@Override
	public boolean getEngaged(Stepper s) {
		return engaged[s.ordinal()];
	}

    /**
     * Set the engaged state of a motor.
	 * An ENGAGED PropertyChangeEvent is fired with the old and new state. 
     * @param s Stepper
     * @param state true=engaged, false=disengaged
     */
	@Override
	public void setEngaged (Stepper s, boolean state) {
		try {
			stepPhidget.setEngaged(s.ordinal(), state);
			boolean old = engaged[s.ordinal()];
			engaged[s.ordinal()] = state;
			pcs.firePropertyChange(Props.ENGAGED.toString(), old, state);
		} catch (PhidgetException ex) {
			showError("StepperBoard.setEngaged", ex);
		}
		stopCheck(s);	// make sure the table is updated
	}

    /**
     * Check to see if a motor is stopped and update the table.
	 * The board is polled to determine the current status. 
	 * A STOPPED PropertyChangeEvent is fired with the old and new state. 
     * @param s Stepper
     * @return true=stopped
     */
	@Override
	public boolean stopCheck(Stepper s) {
		boolean isStopped = false;
		try {
			isStopped = stepPhidget.getStopped(s.ordinal());
//			System.out.println("stopCheck " + s.ordinal() + " " + stopped);
			boolean old = stopped[s.ordinal()];
			stopped[s.ordinal()] = isStopped;
			pcs.firePropertyChange(Props.STOPPED.toString(), old, isStopped);
		} catch (PhidgetException ex) {
			showError("StepperBoard.stopCheck", ex);
		}
		return isStopped;
	}

    /**
     * Disengage all motors.
	 * An ENGAGED PropertyChangeEvent is fired (with null for old and 'false' for new). 
     */
	@Override
	public void disengageAll() {
		try {
			for (int i = 0; i < nSteppers; i++) {
				stepPhidget.setEngaged(i, false);
				engaged[i] = false;
			}
			pcs.firePropertyChange(Props.ENGAGED.toString(), null, false);
		} catch (PhidgetException ex) {
			showError("StepperBoard.disengageAll", ex);
		}
	}

    /**
     * Get the maximum velocity allowed (all motors are the same).
     * @return maximum velocity allowed
     */
	@Override
	public double getMaxVelocity() {
		return maxVelocity;
	}

    /**
     * Get the minimum velocity allowed (all motors are the same).
	 * Note that this is not zero, but rather the minimum resolution.
     * @return minimum velocity allowed
     */
	@Override
	public double getMinVelocity() {
		return minVelocity;
	}

    /**
     * Get the maximum acceleration allowed (all motors are the same).
     * @return maximum acceleration allowed
     */
	@Override
	public double getMaxAcceleration() {
		return maxAccel;
	}

    /**
     * Get the minimum acceleration allowed (all motors are the same).
	 * Note that this is not zero, but rather the minimum resolution.
     * @return minimum acceleration allowed
     */
	@Override
	public double getMinAcceleration() {
		return minAccel;
	}
	
	/**
	 * Get the acceleration setting of the motor
     * @param s Stepper
	 * @return acceleration
	 */
	@Override
	public double getAcceleration(Stepper s) {
		return aLimit[s.ordinal()];
	}

    /**
     * Set the acceleration of a motor.
	 * Note: this will limited by min resolution and max limit.
	 * A ALIMIT PropertyChangeEvent is fired with the old and new values. 
     * @param s Stepper
     * @param val acceleration
     */
	@Override
	public void setAcceleration(Stepper s, double val) {
		try {
//			System.out.println("    setAcceleration: " + s.toString() + " " + val);
			double a = Math.max(Math.min(val, maxAccel), minAccel);
			stepPhidget.setAcceleration(s.ordinal(), a);
			double old = aLimit[s.ordinal()];
			aLimit[s.ordinal()] = a;
			pcs.firePropertyChange(Props.ALIMIT.toString(), old, a);
		} catch (PhidgetException ex) {
			showError("StepperBoard.setAcceleration", ex);
		}
	}

    /**
     * Set the velocity limit of a motor.
	 * Note: this will limited by min resolution and max limit.
	 * A VLIMIT PropertyChangeEvent is fired with the old and new values. 
     * @param s Stepper
     * @param val Velocity limit
     */
	@Override
	public void setVelocityLimit(Stepper s, double val) {
		try {
//			System.out.println("    setVelocity: " + Step.values()[n] + " " + val);
			double v = Math.max(Math.min(val, maxVelocity), minVelocity);
			stepPhidget.setVelocityLimit(s.ordinal(), v);
			double old = vLimit[s.ordinal()];
			vLimit[s.ordinal()] = v;
			pcs.firePropertyChange(Props.VLIMIT.toString(), old, v);
		} catch (PhidgetException ex) {
			showError("StepperBoard.setVelocityLimit", ex);
		}
	}

	/**
	 * Get the previously set velocity limit of a motor.
	 * Note that this is retrieved from an array of saved states to save time.
	 * @param s Stepper
	 * @return velocity limit
	 */
	@Override
	public double getVelocityLimit(Stepper s) {
		return vLimit[s.ordinal()];
	}

    /**
     * Set the motor velocity limit to the maximum.
	 * A VLIMIT PropertyChangeEvent is fired with the old and new values. 
     * @param s Stepper
     */
	@Override
    public void setVelocityToMax(Stepper s) {
        setVelocityLimit(s, maxVelocity);
    }

    /**
     * Get the current position from the board.
	 * The board is polled to determine the current position. 
	 * (which takes longer than reading the table values).
     * @param s Stepper
     * @return Current position
     */
	@Override
	public long getCurrentPosition(Stepper s) {
		long pos = 0;
		try {
			pos = stepPhidget.getCurrentPosition(s.ordinal());	// *** This takes longer than just returning
		} catch (PhidgetException ex) {					// *** the table value!
			showError("StepperBoard.getCurrentPosition", ex);
		}
		return pos;
	}

    /**
     * Get the current position from the array.
	 * Note that this is retrieved from an array of saved states to save time,
	 * and doesn't run the risk of generating another stopped motor indication.
     * @param s Stepper
     * @return Current position
     */
	@Override
	public long getPosition(Stepper s) {
		return position[s.ordinal()];
	}

    /**
     * Set the current position of a motor.
	 * A POSITION PropertyChangeEvent is fired with the old and new values.
     * @param s Stepper
     * @param val New current position
     */
	@Override
	public void setCurrentPosition(Stepper s, int val) {
		try {
			long old = position[s.ordinal()];
			position[s.ordinal()] = val;
			pcs.firePropertyChange(Props.POSITION.toString(), old, val);
			stepPhidget.setCurrentPosition(s.ordinal(), val);
		} catch (PhidgetException ex) {
			showError("StepperBoard.setCurrentPosition", ex);
		}
	}

    /**
     * Get the target position from the board.
	 * The board is polled to determine the current position. 
	 * (which takes longer than reading the table values).
     * @param s Stepper
     * @return Target position
     */
	@Override
	public long getTargetPosition(Stepper s) {
		long tar = 0;
		try {
			tar = stepPhidget.getTargetPosition(s.ordinal());		// *** This takes longer than just returning
		} catch (PhidgetException ex) {					// *** the table value!
			showError("StepperBoard.getTargetPosition", ex);
		}
		return tar;
	}

    /**
     * Set the target position of a motor.
	 * A TARGET PropertyChangeEvent is fired with the old and new values.
	 * Also, if the new target position is different than the current position,
	 * a STOPPED PropertyChangeEvent is fired with the old state and 'false' for the new.
     * @param s Stepper
     * @param val Target position
     */
	@Override
	public void setTargetPosition(Stepper s, long val) {
		try {
//			System.out.println("    setTarget: " + Step.values()[n] + " " + val);
			long old = target[s.ordinal()];
			target[s.ordinal()] = val;
			pcs.firePropertyChange(Props.TARGET.toString(), old, val);
			if (val != position[s.ordinal()]) {					// force NOT STOPPED
				boolean oldStop = stopped[s.ordinal()];			// so that we must get a positionChange
				stopped[s.ordinal()] = false;					// event to insure the motor is stopped
				pcs.firePropertyChange(Props.STOPPED.toString(), oldStop, false);
			}													
			stepPhidget.setTargetPosition(s.ordinal(), val);
		} catch (PhidgetException ex) {
			showError("StepperBoard.setTargetPosition", ex);
		}
	}
	
	/**
	 * Get the current limit of a motor.
	 * The board is polled to determine the current position. 
	 * (which takes longer than reading the table values).
     * @param s Stepper
	 * @return current limit
	 */
	@Override
	public double getCurrentLimit(Stepper s) {
		return cLimit[s.ordinal()];
	}
	
	/**
	 * Set the current limit of a motor.
	 * Note: this will limited by the max limit.
	 * A CLIMIT PropertyChangeEvent is fired with the old and new values.
     * @param s Stepper
     * @param cur Current limit
	 */
	@Override
	public void setCurrentLimit(Stepper s, double cur) {
		try {
//			System.out.println("    setCurrentLimit: " + Step.values()[n] + " " + cur);
			double c = Math.min(cur, MAX_CURRENT[type]);
			stepPhidget.setCurrentLimit(s.ordinal(), c);
			double old = cLimit[s.ordinal()];
			cLimit[s.ordinal()] = c;
			pcs.firePropertyChange(Props.CLIMIT.toString(), old, c);
		} catch (PhidgetException ex) {
			showError("StepperBoard.setCurrentLimit", ex);
		}
	}

	/**
	 * Disengage all steppers and close the Phidget.
	 */
	@Override
	public void close() {
		try {
			for (int i = 0; i < nSteppers; i++) {
				 stepPhidget.setEngaged(i, false);
			}
			stepPhidget.close();      //close the phidget
			stepPhidget = null;
		} catch (PhidgetException ex) {
			showError("StepperBoard.close", ex);
		}
	}

    /**
     * Show an error from a try-catch.
	 * @param st String to display
     * @param ex PhidgetException
     */
	private void showError(String st, PhidgetException ex) {
		if (ex.getErrorNumber() == PhidgetException.EPHIDGET_UNKNOWNVAL) {
			return;		// don't show this
		}
		NotifyDescriptor d = new NotifyDescriptor.Message("1062 " + st + "\n" + ex.getDescription(), NotifyDescriptor.ERROR_MESSAGE);
		DialogDisplayer.getDefault().notify(d);
	}

	/**
	 * Add a PropertyChangeListener.
	 * @param listener
	 */
	@Override
	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener.
	 * @param listener
	 */
	@Override
	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

}
