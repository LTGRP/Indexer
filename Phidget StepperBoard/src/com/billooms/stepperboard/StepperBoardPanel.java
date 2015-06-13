
package com.billooms.stepperboard;

import com.billooms.stepperboard.api.StepperBoard;
import com.phidgets.PhidgetException;
import com.phidgets.StepperPhidget;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * StepperBoard is used to monitor the state of a Phidgets stepper board
 * This is specifically written for a Phidgets 1062 and 1063 boards.
 * 1067 board is EXPERIMENTAL and untested.
 * Note: no checking is done to see if setting a new location is the same as the current location.
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
public class StepperBoardPanel extends JPanel implements PropertyChangeListener {
	private final static int STEPPER=0, ENGAGED=1,  STOPPED=2,  POSITION=3,
							 TARGET=4, VELOCITY=5, V_LIMIT=6, ACCEL=7;	// table column definitions
	
	private StepperBoard stepBoard;
	private TableModel table;

	/**
	 * Create a new StepperBoardPanel for the given StepperBoard.
	 * @param board StepperBoard
	 */
    public StepperBoardPanel(StepperBoard board) {
		this.stepBoard = board;

        initComponents();
		
		table = stepTable.getModel();		// initialize data in column 1 of table
		for (int i = 0; i < stepBoard.getNSteppers(); i++) {
			table.setValueAt(StepperBoard.Stepper.values()[i].toString(), i, STEPPER);
		}
    }

	/**
	 * Listen for PropertyChangeEvents.
	 * @param evt event
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String changed = evt.getPropertyName();
		switch(StepperBoard.Props.valueOf(changed)) {
			case INPUT:
				updateInputs();
				break;
			case POSITION:
				updatePositions();
				break;
			case STOPPED:
				updateStopped();
				break;
			case VELOCITY:
				updateVelocities();
				break;
			case ALL_STOPPED:
				movingLabel.setEnabled(false);
				break;
			case ENGAGED:
				updateEngaged();
				break;
			case VLIMIT:
				updateVLimits();
				break;
			case ALIMIT:
				updateALimits();
				break;
			case CURRENT:
				updateCurrents();
				break;
			case CLIMIT:
				updateCLimits();
				break;
			case TARGET:
				updateTargets();
				break;
			case ATTACH:
			case DETACH:
				updateAll();
				break;
		}
	}

	/**
	 * Update the table with the saved input data
	 */
	private void updateInputs() {
		boolean[] inputs = stepBoard.getInStates();
		for (int i = 0; i < inputs.length; i++) {
			ioTable.setValueAt(inputs[i], i, 1);	// update the display
		}
	}

	/**
	 * Update the table with the saved position data.
	 */
	private void updatePositions() {
		long[] pos = stepBoard.getPositions();
		for (int i = 0; i < pos.length; i++) {
			table.setValueAt(pos[i], i, POSITION);
		}
	}

	/**
	 * Update the table with the saved stopped data.
	 */
	private void updateStopped() {
		boolean[] stopped = stepBoard.getStopped();
		boolean moving = false;
		for (int i = 0; i < stopped.length; i++) {
			table.setValueAt(stopped[i], i, STOPPED);
			if ((Boolean)table.getValueAt(i, ENGAGED) && !stopped[i]) {
				moving = true;
				break;
			}
		}
		movingLabel.setEnabled(moving);		// yellow light when moving
	}

	/**
	 * Update the table with the saved velocity data.
	 */
	private void updateVelocities() {
		double[] vels = stepBoard.getVelocities();
		for (int i = 0; i < vels.length; i++) {
			table.setValueAt(vels[i], i, VELOCITY);
		}
	}

	/**
	 * Update the table with the saved engaged data.
	 */
	private void updateEngaged() {
		boolean[] eng = stepBoard.getEngaged();
		for (int i = 0; i < eng.length; i++) {
			table.setValueAt(eng[i], i, ENGAGED);
		}
	}

	/**
	 * Update the table with the saved velocity limit data.
	 */
	private void updateVLimits() {
		double[] vLim = stepBoard.getVLimits();
		for (int i = 0; i < vLim.length; i++) {
			table.setValueAt(vLim[i], i, V_LIMIT);
		}
	}

	/**
	 * Update the table with the saved acceleration limit data.
	 */
	private void updateALimits() {
		double[] aLim = stepBoard.getALimits();
		for (int i = 0; i < aLim.length; i++) {
			table.setValueAt(aLim[i], i, ACCEL);
		}
	}

	/**
	 * Update the table with the saved target data.
	 */
	private void updateTargets() {
		long[] tar = stepBoard.getTargets();
		for (int i = 0; i < tar.length; i++) {
			table.setValueAt(tar[i], i, TARGET);
		}
	}

	/**
	 * Update the table with the saved current limit data.
	 */
	private void updateCLimits() {
		double[] curl = stepBoard.getCLimits();
		if (curl.length > 0) {
			cLimitField.setText(Double.toString(curl[0]));
		}
	}

	/**
	 * Update the table with the saved current data.
	 */
	private void updateCurrents() {
		double[] cur = stepBoard.getCurrents();
		if (cur.length > 0) {
			curField.setText(Double.toString(cur[0]));
		}
	}

	/**
	 * Update all the fields (for attaching and detaching).
	 */
	public void updateAll() {
		if (stepBoard.isAttached()) {
			StepperPhidget attached = stepBoard.getPhidget();
			try {
				attachedLight.setEnabled(attached.isAttached());	// green light when attached
				nameField.setText(attached.getDeviceName());		// update the text fields
				snField.setText(Integer.toString(attached.getSerialNumber()));
				verField.setText(Integer.toString(attached.getDeviceVersion()));
				labelField.setText(attached.getDeviceLabel());
				nMotorField.setText(Integer.toString(attached.getMotorCount()));
				nInputField.setText(Integer.toString(attached.getInputCount()));
				vMinField.setText(Double.toString(attached.getVelocityMin(0)));		// We assume all 4 motors have
				vMaxField.setText(Double.toString(attached.getVelocityMax(0)));		// the same min & max
				aMinField.setText(Double.toString(attached.getAccelerationMin(0)));
				aMaxField.setText(Double.toString(attached.getAccelerationMax(0)));

				updateEngaged();
				updateStopped();
				updatePositions();
				updateTargets();
				updateVelocities();
				updateVLimits();
				updateALimits();
				updateCurrents();
				updateCLimits();
				updateInputs();
			} catch (PhidgetException ex) {
				NotifyDescriptor d = new NotifyDescriptor.Message(ex.getDescription(), NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(d);
			}
		} else {
			attachedLight.setEnabled(false);	// no more green light
			nameField.setText("");				// set all text fields to blank
			snField.setText("");
			verField.setText("");
			labelField.setText("");
			nMotorField.setText("");
			nInputField.setText("");

			for (int i = 0; i < stepBoard.getNSteppers(); i++) {	// clear out the table on disconnect
					table.setValueAt(false, i, ENGAGED);
					table.setValueAt(false, i, STOPPED);
					table.setValueAt("", i, POSITION);
					table.setValueAt("", i, TARGET);
					table.setValueAt(0.0, i, VELOCITY);
					table.setValueAt(0.0, i, V_LIMIT);
					table.setValueAt(0.0, i, ACCEL);
			}
			curField.setText("");
			cLimitField.setText("");
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        snField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        verField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        labelField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        nMotorField = new javax.swing.JTextField();
        attachedLight = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        nInputField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        vMinField = new javax.swing.JTextField();
        vMaxField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        aMinField = new javax.swing.JTextField();
        aMaxField = new javax.swing.JTextField();
        movingLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stepTable = new javax.swing.JTable();
        ioPane = new javax.swing.JScrollPane();
        ioTable = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        curField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        cLimitField = new javax.swing.JTextField();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jPanel1.border.title"))); // NOI18N

        jLabel1.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel1.text")); // NOI18N

        nameField.setEditable(false);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel2.text")); // NOI18N

        snField.setColumns(5);
        snField.setEditable(false);

        jLabel3.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel3.text")); // NOI18N

        verField.setColumns(5);
        verField.setEditable(false);

        jLabel4.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel4.text")); // NOI18N

        labelField.setColumns(5);
        labelField.setEditable(false);

        jLabel5.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel5.text")); // NOI18N

        nMotorField.setColumns(3);
        nMotorField.setEditable(false);

        attachedLight.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/stepperboard/green_20.png"))); // NOI18N
        attachedLight.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.attachedLight.text")); // NOI18N
        attachedLight.setEnabled(false);
        attachedLight.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        jLabel6.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel6.text")); // NOI18N

        nInputField.setColumns(3);
        nInputField.setEditable(false);

        jLabel7.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel7.text")); // NOI18N

        vMinField.setColumns(6);
        vMinField.setEditable(false);
        vMinField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        vMaxField.setColumns(6);
        vMaxField.setEditable(false);
        vMaxField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel9.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel9.text")); // NOI18N

        jLabel10.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel10.text")); // NOI18N

        jLabel11.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel11.text")); // NOI18N

        aMinField.setColumns(6);
        aMinField.setEditable(false);
        aMinField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        aMaxField.setColumns(6);
        aMaxField.setEditable(false);
        aMaxField.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        movingLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/billooms/stepperboard/yellow_20.png"))); // NOI18N
        movingLabel.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.movingLabel.text")); // NOI18N
        movingLabel.setEnabled(false);
        movingLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(vMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(aMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(aMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(movingLabel))
                        .addComponent(nameField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(snField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(verField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(nMotorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(nInputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(labelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(attachedLight))))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(jLabel9)
                .addGap(68, 68, 68)
                .addComponent(jLabel10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(snField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(verField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(labelField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(nMotorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(nInputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attachedLight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(vMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(aMinField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aMaxField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(movingLabel))
                .addContainerGap())
        );

        stepTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, new Boolean(false), new Boolean(false), null, null, null, null, null},
                {null, new Boolean(false), new Boolean(false), null, null, null, null, null},
                {null, new Boolean(false), new Boolean(false), null, null, null, null, null},
                {null, new Boolean(false), new Boolean(false), null, null, null, null, null}
            },
            new String [] {
                "Step", "Eng", "Stop", "Position", "Target", "Velocity", "vLimit", "Accel"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Long.class, java.lang.Long.class, java.lang.Float.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stepTable.setRowSelectionAllowed(false);
        stepTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(stepTable);
        stepTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        stepTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title0")); // NOI18N
        stepTable.getColumnModel().getColumn(1).setPreferredWidth(30);
        stepTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title1")); // NOI18N
        stepTable.getColumnModel().getColumn(2).setPreferredWidth(30);
        stepTable.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title2")); // NOI18N
        stepTable.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title3")); // NOI18N
        stepTable.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title4")); // NOI18N
        stepTable.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title5")); // NOI18N
        stepTable.getColumnModel().getColumn(6).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title6")); // NOI18N
        stepTable.getColumnModel().getColumn(7).setHeaderValue(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.stepTable.columnModel.title7")); // NOI18N

        ioTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {new Integer(0), new Boolean(true)},
                {new Integer(1), new Boolean(true)},
                {new Integer(2), new Boolean(true)},
                {new Integer(3), new Boolean(true)}
            },
            new String [] {
                "", "In"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ioTable.getTableHeader().setReorderingAllowed(false);
        ioPane.setViewportView(ioTable);

        jLabel17.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel17.text")); // NOI18N

        curField.setColumns(6);
        curField.setEditable(false);
        curField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        curField.setToolTipText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.curField.toolTipText")); // NOI18N

        jLabel16.setText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.jLabel16.text")); // NOI18N

        cLimitField.setColumns(6);
        cLimitField.setEditable(false);
        cLimitField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cLimitField.setToolTipText(org.openide.util.NbBundle.getMessage(StepperBoardPanel.class, "StepperBoardPanel.cLimitField.toolTipText")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ioPane, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(curField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cLimitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(91, 91, 91))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cLimitField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(curField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)))
                    .addComponent(ioPane, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aMaxField;
    private javax.swing.JTextField aMinField;
    private javax.swing.JLabel attachedLight;
    private javax.swing.JTextField cLimitField;
    private javax.swing.JTextField curField;
    private javax.swing.JScrollPane ioPane;
    private javax.swing.JTable ioTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField labelField;
    public javax.swing.JLabel movingLabel;
    private javax.swing.JTextField nInputField;
    private javax.swing.JTextField nMotorField;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField snField;
    private javax.swing.JTable stepTable;
    private javax.swing.JTextField vMaxField;
    private javax.swing.JTextField vMinField;
    private javax.swing.JTextField verField;
    // End of variables declaration//GEN-END:variables
}
