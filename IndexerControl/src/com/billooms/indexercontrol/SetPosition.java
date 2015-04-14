
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
