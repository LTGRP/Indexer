/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.billooms.indexerfiletype;

import com.billooms.indexwheel.api.IndexWheelMgr;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

/**
 * Action to save an Indexer xml file with a new name
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ActionID(category = "File",
id = "com.billooms.indexerfiletype.SaveAction")
@ActionRegistration(displayName = "#CTL_SaveAction")
@ActionReferences({
	@ActionReference(path = "Menu/File", position = 1200),
	@ActionReference(path = "Shortcuts", name = "S-D-S")
})
@Messages("CTL_SaveAction=Save XML File As...")
public final class SaveAsFile implements ActionListener {
	private final static String EXTENSION = "xml";
	private final static String DEFAULT_NAME = "IndexWheel";

	/**
	 * Action to save an Indexer XML file with a new name
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		saveAs();
	}

	/**
	 * Save and xml file with a new name
	 */
	protected static void saveAs() {
		File home = new File (System.getProperty("user.home"));		//The default dir to use if no value is stored
		File saveFile;

		JFileChooser chooser = new FileChooserBuilder("openfile")	// "openfile" is key for NbPreferences
				.setTitle("Save File As...")
				.setDefaultWorkingDirectory(home)					// only if a last-used directory cannot be found for the key
				.setApproveText("Save")
				.setFileFilter(new FileNameExtensionFilter(EXTENSION + " files", EXTENSION))
				.createFileChooser();

		if (OpenFile.openedFile != null) {		// offer the same name and incremented suffix
			if (OpenFile.suffix == 0) {
				OpenFile.suffix++;				// increment suffix
				saveFile = new File(OpenFile.openedFile.toString().replace("." + EXTENSION, "_" + OpenFile.suffix + "." + EXTENSION));
			} else {
				saveFile = new File(OpenFile.openedFile.toString().replace("_" + OpenFile.suffix , "_" + (OpenFile.suffix +1)));
				OpenFile.suffix++;				// increment suffix
			}
		} else {
			saveFile = new File(DEFAULT_NAME + "." + EXTENSION);
		}

		chooser.setSelectedFile(saveFile);
        int option = chooser.showSaveDialog(null);
        if (option != JFileChooser.APPROVE_OPTION) {
			return;										// User canceled or clicked the dialog's close box.
        }
        saveFile = chooser.getSelectedFile();

		if (!(saveFile.toString()).endsWith("." + EXTENSION)) {	// make sure we have format right
			saveFile = new File(saveFile.toString() + "." + EXTENSION);
		}
		if (saveFile.exists()) {					// Ask the user whether to replace the file.
			NotifyDescriptor d = new NotifyDescriptor.Confirmation(
					"The file " + saveFile.getName() + " already exists.\nDo you want to replace it?",
					"Overwrite File Check",
					NotifyDescriptor.YES_NO_OPTION,
					NotifyDescriptor.WARNING_MESSAGE);
			d.setValue(NotifyDescriptor.CANCEL_OPTION);
			Object result = DialogDisplayer.getDefault().notify(d);
			if (result != DialogDescriptor.YES_OPTION) {
				return;
			}
		}
		
		IndexWheelMgr mgr = Lookup.getDefault().lookup(IndexWheelMgr.class);
		if (mgr != null) {
			mgr.writeXML(saveFile);		// force the IndexWheelMgr to write to a new file
			StatusDisplayer.getDefault().setStatusText("Saving File As: " + saveFile.getName());
			
			// Find the DataObject for the currently open file and forget changes
			// so that Save All... doesn't try to save things later
			if (OpenFile.openedFile != null) {
				FileObject openFO = FileUtil.toFileObject(FileUtil.normalizeFile(OpenFile.openedFile));
				try {
					IndexerFileDataObject openDO = (IndexerFileDataObject) DataObject.find(openFO);
					openDO.forgetChanges();
				} catch (DataObjectNotFoundException dataObjectNotFoundException) {
					Exceptions.printStackTrace(dataObjectNotFoundException);
				}
			}
			
			OpenFile.open(saveFile);	// Now open the new file so everyone is happy
		}
	}
}
