/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.billooms.indexerfiletype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Action to open an Indexer xml file
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ActionID(category = "File",
id = "com.billooms.indexerfiletype.OpenFile")
@ActionRegistration(iconBase = "com/billooms/indexerfiletype/openFile.png",
displayName = "#CTL_OpenFile")
@ActionReferences({
	@ActionReference(path = "Menu/File", position = 1000),
	@ActionReference(path = "Shortcuts", name = "D-O"),
	@ActionReference(path = "Toolbars/File", position = 300)
})
@Messages("CTL_OpenFile=Open XML File...")
public final class OpenFile implements ActionListener {
	private final static String EXTENSION = "xml";

	/** The most recently opened file */
	protected static File openedFile = null;
	/** Numerical suffix for future versions of the file */
	protected static int suffix = 0;			// suffix for versions
	
	protected static DataObject dObj = null;	// DataObject for openedFile

	/**
	 * Action to open an Indexer XML file
	 * @param e ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if ((dObj != null) && dObj.isModified()) {		// Any unsaved changes?
			NotifyDescriptor d = new NotifyDescriptor.Confirmation(
					"Changes have not been saved!\nDo you want to save changes?",
					"Changes not saved",
					NotifyDescriptor.YES_NO_CANCEL_OPTION,
					NotifyDescriptor.WARNING_MESSAGE);
			d.setValue(NotifyDescriptor.YES_OPTION);
			Object result = DialogDisplayer.getDefault().notify(d);
			if ((result == DialogDescriptor.CLOSED_OPTION) || (result == DialogDescriptor.CANCEL_OPTION)) {
				return;										// Cancel/Close: don't open a new file
			}
			if (result == DialogDescriptor.YES_OPTION) {
				SaveFile.save();				// YES: save and continue
			}	
			dObj.setModified(false);			// NO: don't save changes, continue to open a new file
		}
		
		File home = new File (System.getProperty("user.home"));	//The default dir to use if no value is stored
		File file = new FileChooserBuilder("openfile")		// "openfile" is key for NbPreferences
				.setTitle("Open File")
				.setDefaultWorkingDirectory(home)			// only if a last-used directory cannot be found for the key
				.setApproveText("Open")
				.setFileFilter(new FileNameExtensionFilter(EXTENSION + " files", EXTENSION))
				.showOpenDialog();
		if (file != null) {
			suffix = findSuffix(file);
			open(file);
		}
	}

	/**
	 * Find the numerical suffix: the number xx in "name_xx.xml"
	 * @param file the given file
	 * @return the numerical suffix, or 0 if there is none
	 */
	private static int findSuffix(File file) {
		String str = file.getName().replace("." + EXTENSION, "");
		int i = str.lastIndexOf("_");
		if (i < 0) {
			return 0;
		}
		int suf;
		try {
			suf = Integer.parseInt(str.substring(i+1));
		} catch (Exception e) {
			suf = 0;
		}
		return suf;
	}

	/**
	 * Open the given file, create a DataObject for the file, 
	 * and read the xml into the IndexWheelMgr.
	 * @param file File to open
	 */
	protected static void open(File file) {
		if (file != null) {
			FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(file));
			if (!fo.getMIMEType().equals(NbBundle.getMessage(OpenFile.class, "MIMETYPE"))) {
				NotifyDescriptor d = new NotifyDescriptor.Message(
						"File " + file.getName() + " is not an Indexer xml file!",
						NotifyDescriptor.ERROR_MESSAGE);
				DialogDisplayer.getDefault().notify(d);
				return;
			}
			try {
				dObj = DataObject.find(fo);		// this creates the DataObject for the file
				
				OpenCookie openCookie = dObj.getLookup().lookup(OpenCookie.class);
				if (openCookie != null) {		// this should always be the case
					openCookie.open();			// this is what reads the xml file to the IndexerMgr
					openedFile = file;			// this is the last file opened
					StatusDisplayer.getDefault().setStatusText("Open File: " + file.getName());
				}
			} catch (DataObjectNotFoundException ex) {
				Exceptions.printStackTrace(ex);
			}
		}
	}
}
