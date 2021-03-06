
package com.billooms.indexerfiletype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

/**
 * Revert to the last saved version of this file
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
@ActionID(category = "File",
id = "com.billooms.indexerfiletype.Revert")
@ActionRegistration(displayName = "#CTL_Revert")
@ActionReferences({
	@ActionReference(path = "Menu/File", position = 1300)
})
@Messages("CTL_Revert=Revert to Saved File")
public final class Revert implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		if ((OpenFile.dObj != null) && OpenFile.dObj.isModified()) {		// Any unsaved changes?
			NotifyDescriptor d = new NotifyDescriptor.Confirmation(
					"Changes have not been saved!\nDo you want to discard changes?",
					"Changes not saved",
					NotifyDescriptor.OK_CANCEL_OPTION,
					NotifyDescriptor.WARNING_MESSAGE);
			d.setValue(NotifyDescriptor.OK_OPTION);
			Object result = DialogDisplayer.getDefault().notify(d);
			if (result != DialogDescriptor.OK_OPTION) {
				return;									// Cancel/Close: don't revert
			}
			OpenFile.dObj.setModified(false);			// OK: don't save changes, open the file again
		}
		
		OpenFile.open(OpenFile.openedFile);
	}
}
