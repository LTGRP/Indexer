
package com.billooms.indexercontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 * Action to set the current position of the spindle to a new value.
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ActionID(category = "Edit",
id = "com.billooms.indexercontrol.SetPosition")
@ActionRegistration(displayName = "#CTL_SetPosition")
@ActionReferences({
	@ActionReference(path = "Menu/Control", position = 3633)
})
@Messages("CTL_SetPosition=Set Position to...")
public final class SetPosition extends AbstractAction implements ActionListener, Presenter.Menu {
	private JMenuItem menuItem;

	@Override
	public void actionPerformed(ActionEvent e) {
		SetPositionPanel panel = new SetPositionPanel();
		
		DialogDescriptor dd = new DialogDescriptor(
				panel,
				"Set Stage Position",
				true,
				DialogDescriptor.OK_CANCEL_OPTION,
				DialogDescriptor.OK_OPTION,
				null);
		Object result = DialogDisplayer.getDefault().notify(dd);
		if (result == DialogDescriptor.OK_OPTION) {
			panel.doit();
		}
	}

	@Override
	public JMenuItem getMenuPresenter() {
		if (this.menuItem == null) {
			menuItem = new JMenuItem(NbBundle.getMessage(SetPosition.class, "CTL_SetPosition"));
            menuItem.addActionListener(this);
		}
		return this.menuItem;
	}
}
