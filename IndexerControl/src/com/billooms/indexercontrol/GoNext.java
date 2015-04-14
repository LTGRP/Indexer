
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
 * Action to move the spindle to the next index point. 
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ActionID(category = "Edit",
id = "com.billooms.indexercontrol.GoNext")
@ActionRegistration(displayName = "#CTL_GoNext")
@ActionReferences({
	@ActionReference(path = "Menu/Control", position = 3433),
	@ActionReference(path = "Shortcuts", name = "D-N")
})
@Messages("CTL_GoNext=Go To Next")
public final class GoNext implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		WindowManager.getDefault().findTopComponent("ControlTopComponent").getLookup().lookup(ControlPanel.class).goNext();
	}
}
