
package com.billooms.stepperboard;

import com.billooms.stepperboard.api.StepperBoard;
import java.awt.BorderLayout;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;

/**
 * Top component for displaying the status of a Phidgets 1062 board.
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
@ConvertAsProperties(dtd = "-//com.billooms.stepperboard//Phidget//EN",
autostore = false)
@TopComponent.Description(preferredID = "PhidgetTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Window", id = "com.billooms.stepperboard.PhidgetTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_PhidgetAction",
preferredID = "PhidgetTopComponent")
public final class PhidgetTopComponent extends TopComponent {

	private StepperBoard stepBoard;
	private StepperBoardPanel panel;

	/** Creates a new PhidgetTopComponent */
	public PhidgetTopComponent() {
		initComponents();
		setName(NbBundle.getMessage(PhidgetTopComponent.class, "CTL_PhidgetTopComponent"));
		setToolTipText(NbBundle.getMessage(PhidgetTopComponent.class, "HINT_PhidgetTopComponent"));

		stepBoard = Lookup.getDefault().lookup(StepperBoard.class);
		panel = new StepperBoardPanel(stepBoard);
		this.add(panel, BorderLayout.CENTER);
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
		stepBoard.addPropertyChangeListener(panel);	// listen to the StepperBoard
		panel.updateAll();
	}

	@Override
	public void componentClosed() {
		stepBoard.removePropertyChangeListener(panel);	// quit listening to the StepperBoard
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
