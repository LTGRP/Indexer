
package com.billooms.indexercontrol;

import com.billooms.indexerprefs.api.Preferences;
import com.billooms.indexwheel.api.IndexWheel;
import com.billooms.indexwheel.api.IndexWheelMgr;
import com.billooms.stepperboard.api.StepperBoard;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Control panel for controlling the stepper motor
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
public class ControlPanel extends JPanel implements PropertyChangeListener, LookupListener {
	
	private final static double BIG_ROTATE = 10 * 360.0;	// for manual control
	private final static double MAX_RPM = 10.0;				// max rpm (unless limited by hardware to a smaller value)
	
	private static int stepsPerRotation;	// micro-steps per rotation
	private RotationStage cStage;
	private double rpm;					// speed set by the slider
	private IndexWheel selected = null;		// currenly selected IndexWheel
	private IndexWheelMgr idxMgr = Lookup.getDefault().lookup(IndexWheelMgr.class);	// IndexWheel manager
	
	private static SoundClip ding = null;	// sound for going past zero
	private static double lastPos = 0.0;	// save last position (modulo 360.0)
	
	/** Creates new ControlPanel */
	public ControlPanel(RotationStage stage) {
		this.cStage = stage;
//		this.rpm = cStage.getMaxRPM();		// this always gives 0.0 -- do it on engage instead
		stepsPerRotation = Lookup.getDefault().lookup(Preferences.class).getStepsPerRotation();
		if (ding == null) {
			ding = new SoundClip("Ding.wav");
		}
		
		initComponents();
		
		Hashtable labelTable = new Hashtable();		// labels for slider
		labelTable.put(new Integer(0), new JLabel("Slow"));
		labelTable.put(new Integer(100), new JLabel("Fast"));
		speedSlider.setLabelTable(labelTable);

		updatePosition();		// show the actual value from the stage
	}
	
    /**
     * Listen to the StepperBoard for ALL_STOPPED and for POSITION changes, 
     * and listen to the IndexWheelMgr for PROP_READXML.
     * @param evt 
     */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof StepperBoard) {
			switch(StepperBoard.Props.valueOf(evt.getPropertyName())) {
				case ALL_STOPPED:						// listen for ALL_STOPPED
					nextButton.setSelected(false);		// deselect the buttons
					goZeroButton.setSelected(false);
					updatePosition();					// update the position on the display
					break;
				case POSITION:							// listen for POSITION changes
					updatePosition();					// update the position on the display
					break;
			}
		} else if (evt.getSource() instanceof IndexWheelMgr) {
			if (evt.getPropertyName().equals(IndexWheelMgr.PROP_READXML)) {
				selected = idxMgr.get(0);		// after reading select the first one
				wheelField.setText(selected.getName());
			}
			if (evt.getPropertyName().equals(IndexWheelMgr.PROP_REMOVE)) {
				if (idxMgr.size() == 0) {		// shouldn't happen
					selected = null;
					wheelField.setText("");
					return;
				}
				if (evt.getOldValue() == selected) {	// if deleted, show the first one on the list
					selected = (IndexWheel)evt.getNewValue();
					wheelField.setText(selected.getName());
				}
			}
		}
	}

    /**
     * Listen for changes in the selection of IndexWheels
     * @param evt 
     */
	@Override
	public void resultChanged(LookupEvent evt) {
        Lookup.Result r = (Lookup.Result) evt.getSource();	// should be same as result in ControlTopComponent
        Collection c = r.allInstances();
        if (!c.isEmpty()) {
            selected = (IndexWheel) c.iterator().next();
			wheelField.setText(selected.getName());
        }
	}
	
	/**
	 * Rotate the spindle to the zero position.
	 * This can be called from some external source. 
	 * The button on the panel is selected (to show the status).
	 */
	public void goZero() {
		goZeroButton.setSelected(true);
		goZero(null);
	}
	
	/**
	 * Rotate the spindle to the next index point.
	 * This can be called from some external source. 
	 * The button on the panel is selected (to show the status).
	 */
	public void goNext() {
		nextButton.setSelected(true);
		goNext(null);
	}
	
	/**
	 * Update the display with the current position
	 */
	public final void updatePosition() {
		double rot = cStage.getPosition();
		rotateField.setValue(rot);
		lastPos = rot;						// save last position
		if (selected != null) {
			selected.setRotation(rot);		// rotate to the actual value
		}
	}
	
	/**
	 * Check to see if some IndexWheel was selected. 
	 * If not, use the first IndexWheel from the IndexWheelMgr.
	 * @return true=OK, false=can't find an IndexWheel to select
	 */
	private boolean someWheelSelected() {
		if (selected == null) {
			selected = Lookup.getDefault().lookup(IndexWheelMgr.class).get(0);
			if (selected == null) {
				return false;						// couldn't find one
			}
			wheelField.setText(selected.getName());	// found one, show the name
		}
		return true;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        engageButton = new javax.swing.JToggleButton();
        goZeroButton = new javax.swing.JToggleButton();
        nextButton = new javax.swing.JToggleButton();
        jPanel1 = new javax.swing.JPanel();
        speedSlider = new javax.swing.JSlider();
        cwButton = new javax.swing.JButton();
        ccwButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        wheelField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        rotateField = new javax.swing.JFormattedTextField();

        setEnabled(false);

        engageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/Engage.png"))); // NOI18N
        engageButton.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.engageButton.text")); // NOI18N
        engageButton.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.engageButton.toolTipText")); // NOI18N
        engageButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/Engaged.png"))); // NOI18N
        engageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                engage(evt);
            }
        });

        goZeroButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/Zero.png"))); // NOI18N
        goZeroButton.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.goZeroButton.text")); // NOI18N
        goZeroButton.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.goZeroButton.toolTipText")); // NOI18N
        goZeroButton.setEnabled(false);
        goZeroButton.setFocusable(false);
        goZeroButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        goZeroButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/Zeroing.png"))); // NOI18N
        goZeroButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        goZeroButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goZero(evt);
            }
        });

        nextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/Next.png"))); // NOI18N
        nextButton.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.nextButton.text")); // NOI18N
        nextButton.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.nextButton.toolTipText")); // NOI18N
        nextButton.setEnabled(false);
        nextButton.setFocusable(false);
        nextButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nextButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/NextGo.png"))); // NOI18N
        nextButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goNext(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.jPanel1.border.title"))); // NOI18N

        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintLabels(true);
        speedSlider.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.speedSlider.toolTipText")); // NOI18N
        speedSlider.setValue(100);
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                changeSpeed(evt);
            }
        });

        cwButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/CW.png"))); // NOI18N
        cwButton.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.cwButton.text")); // NOI18N
        cwButton.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.cwButton.toolTipText")); // NOI18N
        cwButton.setEnabled(false);
        cwButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressCW(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                releaseCW(evt);
            }
        });

        ccwButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/indexercontrol/icons/CCW.png"))); // NOI18N
        ccwButton.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.ccwButton.text")); // NOI18N
        ccwButton.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.ccwButton.toolTipText")); // NOI18N
        ccwButton.setEnabled(false);
        ccwButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressCCW(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                releaseCCW(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ccwButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cwButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(speedSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(ccwButton)
                .addComponent(cwButton))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.jPanel2.border.title_1"))); // NOI18N

        wheelField.setEditable(false);
        wheelField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        wheelField.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.wheelField.text")); // NOI18N
        wheelField.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.wheelField.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(wheelField, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(wheelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.jPanel3.border.title"))); // NOI18N

        rotateField.setColumns(5);
        rotateField.setEditable(false);
        rotateField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        rotateField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rotateField.setText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.rotateField.text")); // NOI18N
        rotateField.setToolTipText(org.openide.util.NbBundle.getMessage(ControlPanel.class, "ControlPanel.rotateField.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rotateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rotateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(engageButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(goZeroButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextButton)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(engageButton)
                    .addComponent(goZeroButton)
                    .addComponent(nextButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

	private void engage(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_engage
		if (!engageButton.isSelected()) {
			if (cStage.getPosition() != 0.0) {
				NotifyDescriptor d = new NotifyDescriptor.Confirmation(
						"Stage is not at zero, Disengage anyway?",
						"Disengage Check",
						NotifyDescriptor.YES_NO_OPTION,
						NotifyDescriptor.WARNING_MESSAGE);
				d.setValue(NotifyDescriptor.NO_OPTION);
				Object result = DialogDisplayer.getDefault().notify(d);
				if (result == DialogDescriptor.NO_OPTION) {
					engageButton.setSelected(true);
					return;
				}
			}
		}
		if (!cStage.setEngaged(engageButton.isSelected())) {
			engageButton.setSelected(false);
			return;		// (there was a problem)
		}
		if (engageButton.isSelected()) {
			rpm = Math.min(cStage.getMaxRPM(), MAX_RPM);
			goZeroButton.setEnabled(true);		// other buttons enabled
			nextButton.setEnabled(true);
			cwButton.setEnabled(true);
			ccwButton.setEnabled(true);
			NotifyDescriptor d = new NotifyDescriptor.Message(
					"Don't forget to set Zero!",
					NotifyDescriptor.INFORMATION_MESSAGE);
			DialogDisplayer.getDefault().notify(d);
		} else {
			goZeroButton.setEnabled(false);		// other buttons disabled
			nextButton.setEnabled(false);
			cwButton.setEnabled(false);
			ccwButton.setEnabled(false);
		}
}//GEN-LAST:event_engage

	private void goZero(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goZero
		if (!someWheelSelected()) {
			goZeroButton.setSelected(false);
			return;
		}
		
		if (engageButton.isSelected()) {
			if (goZeroButton.isSelected()) {
				if (cStage.getPosition() == 0.0) {
					goZeroButton.setSelected(false);	// it's at zero already
				} else {
					cStage.wrapAroundCheck();
//					cStage.goToAtMax(0.0);
					cStage.goToAtRPM(0.0, rpm);
					selected.setRotation(0.0);
				}
			} else {
				cStage.stop();
				selected.setRotation(cStage.getPosition());
			}
		} else {	// this is for playing with the software when no motors connected
			selected.setRotation(0.0);
			rotateField.setValue(0.0);
			lastPos = 0.0;
			goZeroButton.setSelected(false);
		}
}//GEN-LAST:event_goZero

	private void goNext(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goNext
		if (!someWheelSelected()) {
			nextButton.setSelected(false);		// no wheel is selected!
			return;
		}
		
		if (engageButton.isSelected()) {
			if (nextButton.isSelected()) {
				double rot = selected.getRotationOfNext(stepsPerRotation);
				cStage.goToAtRPM(rot, rpm);
				if (((long)rot % 360) < ((long)lastPos % 360)) {	// sound when going past zero
					ding.play();
				}
			} else {
				cStage.stop();
				selected.setRotation(cStage.getPosition());
			}
		} else {	// this is for playing with the software when no motors connected
			double rot = selected.getRotationOfNext(stepsPerRotation);
			selected.setRotation(rot);
			rotateField.setValue(rot);
			lastPos = rot;
			nextButton.setSelected(false);
		}
}//GEN-LAST:event_goNext

	private void pressCW(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressCW
		cStage.goToAtRPM(-BIG_ROTATE, rpm);	// CW is negative
	}//GEN-LAST:event_pressCW

	private void releaseCW(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_releaseCW
		cStage.stop();
		cStage.setVelocityToMax();		// retore max velocity
	}//GEN-LAST:event_releaseCW

	private void pressCCW(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressCCW
		cStage.goToAtRPM(BIG_ROTATE, rpm);	// CCW is positive
	}//GEN-LAST:event_pressCCW

	private void releaseCCW(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_releaseCCW
		cStage.stop();
		cStage.setVelocityToMax();		// retore max velocity
	}//GEN-LAST:event_releaseCCW

	private void changeSpeed(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_changeSpeed
		rpm = Math.max(1, speedSlider.getValue()) * Math.min(cStage.getMaxRPM(), MAX_RPM) / 100.0;
		cStage.setSpindleRPM(rpm);
	}//GEN-LAST:event_changeSpeed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ccwButton;
    private javax.swing.JButton cwButton;
    private javax.swing.JToggleButton engageButton;
    private javax.swing.JToggleButton goZeroButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JToggleButton nextButton;
    private javax.swing.JFormattedTextField rotateField;
    private javax.swing.JSlider speedSlider;
    private javax.swing.JTextField wheelField;
    // End of variables declaration//GEN-END:variables

}
