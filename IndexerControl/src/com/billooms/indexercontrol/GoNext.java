
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
