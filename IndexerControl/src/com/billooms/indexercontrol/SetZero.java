
package com.billooms.indexercontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Set the current location of the rotation Stage to be the new zero.
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ActionID(category = "Edit",
id = "com.billooms.indexercontrol.SetZerp")
@ActionRegistration(displayName = "#CTL_SetZerp")
@ActionReferences({
	@ActionReference(path = "Menu/Control", position = 3533, separatorBefore = 3483)
})
@Messages("CTL_SetZerp=Set Zero")
public final class SetZero implements ActionListener {
 
	@Override
	public void actionPerformed(ActionEvent e) {
		WindowManager.getDefault().findTopComponent("ControlTopComponent").getLookup().lookup(RotationStage.class).setZero();
	}
}
