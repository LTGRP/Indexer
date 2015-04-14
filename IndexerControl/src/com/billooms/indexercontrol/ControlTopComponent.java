
package com.billooms.indexercontrol;

import com.billooms.indexerprefs.api.Preferences;
import com.billooms.indexwheel.api.IndexWheel;
import com.billooms.indexwheel.api.IndexWheelMgr;
import com.billooms.stepperboard.api.StepperBoard;
import com.billooms.stepperboard.api.StepperBoard.Stepper;
import java.awt.BorderLayout;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * Top component for controlling the stepper motor.
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
@ConvertAsProperties(dtd = "-//com.billooms.indexercontrol//Control//EN",
autostore = false)
@TopComponent.Description(preferredID = "ControlTopComponent",
iconBase="com/billooms/indexercontrol/icons/Control16.png", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "control", openAtStartup = true)
@ActionID(category = "Window", id = "com.billooms.indexercontrol.ControlTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ControlAction",
preferredID = "ControlTopComponent")
public final class ControlTopComponent extends TopComponent {

	private RotationStage cStage;	// This is the hardware interface
	private ControlPanel panel;
	
    private Lookup.Result result = null;	// global selection of IndexWheel
	private StepperBoard stepBoard = null;
	private IndexWheelMgr idxMgr = null;
	private Preferences prefs = null;
	
    /**
     * Create a new window for controlling the stepper motor.
     */
	public ControlTopComponent() {
		initComponents();
		setName(NbBundle.getMessage(ControlTopComponent.class, "CTL_ControlTopComponent"));
		setToolTipText(NbBundle.getMessage(ControlTopComponent.class, "HINT_ControlTopComponent"));
		putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.TRUE);
		putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.TRUE);

		cStage = new RotationStage(Stepper.values()[Lookup.getDefault().lookup(Preferences.class).getWiredTo()]);
		
		panel = new ControlPanel(cStage);
		this.add(panel, BorderLayout.CENTER);
		
		this.associateLookup(Lookups.fixed(panel, cStage));
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	@Override
	public void componentOpened() {
		// TODO add custom code on component opening
		stepBoard = Lookup.getDefault().lookup(StepperBoard.class);
		stepBoard.addPropertyChangeListener(panel);				// panel listens to the StepperBoard
		idxMgr = Lookup.getDefault().lookup(IndexWheelMgr.class);
		idxMgr.addPropertyChangeListener(panel);				// panel listens for READXML
		prefs = Lookup.getDefault().lookup(Preferences.class);
		prefs.addPropertyChangeListener(cStage);				// stage listens for setup changes
        result = Utilities.actionsGlobalContext().lookupResult(IndexWheel.class);
        result.addLookupListener(panel);						// panel listens for changes in the selection
	}

	@Override
	public void componentClosed() {
		// TODO add custom code on component closing
		stepBoard.removePropertyChangeListener(panel);		// quit listening when window closes
		stepBoard = null;
		idxMgr.removePropertyChangeListener(panel);
		idxMgr = null;
		prefs.removePropertyChangeListener(cStage);
		prefs = null;
        result.removeLookupListener(panel);	// remove the listener when the window closes
        result = null;
	}

	void writeProperties(java.util.Properties p) {
		// better to version settings since initial version as advocated at
		// http://wiki.apidesign.org/wiki/PropertyFiles
		p.setProperty("version", "1.0");
		// TODO store your settings
	}

	void readProperties(java.util.Properties p) {
		String version = p.getProperty("version");
		// TODO read your settings according to their version
	}
}
