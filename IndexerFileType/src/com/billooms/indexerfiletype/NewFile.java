
package com.billooms.indexerfiletype;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

/**
 * Action to create and open a new file from the template.
 * @author Bill Ooms Copyright (c) 2011 Studio of Bill Ooms all rights reserved
 */
@ActionID(category = "File",
id = "com.billooms.indexerfiletype.NewFile")
@ActionRegistration(displayName = "#CTL_NewFile")
@ActionReferences({
	@ActionReference(path = "Menu/File", position = 900)
})
@Messages("CTL_NewFile=New File...")
public final class NewFile implements ActionListener {
	private final static String EXTENSION = "xml";
	private final static String DEFAULT_NAME = "NewIndexerFile";

	@Override
	public void actionPerformed(ActionEvent e) {
		newFile();
	}
	
	/**
	 * Create a new xml file with contents from the template.
	 */
	protected void newFile() {
		File home = new File (System.getProperty("user.home"));		//The default dir to use if no value is stored
		File newFile;

		JFileChooser chooser = new FileChooserBuilder("openfile")	// "openfile" is key for NbPreferences
				.setTitle("New File...")
				.setDefaultWorkingDirectory(home)					// only if a last-used directory cannot be found for the key
				.setApproveText("Create")
				.setFileFilter(new FileNameExtensionFilter(EXTENSION + " files", EXTENSION))
				.createFileChooser();
		
		newFile = new File(DEFAULT_NAME + "." + EXTENSION);
		chooser.setSelectedFile(newFile);
        int option = chooser.showDialog(null, "Create");
        if (option != JFileChooser.APPROVE_OPTION) {
			return;										// User canceled or clicked the dialog's close box.
        }
        newFile = chooser.getSelectedFile();

		if (!(newFile.toString()).endsWith("." + EXTENSION)) {	// make sure we have format right
			newFile = new File(newFile.toString() + "." + EXTENSION);
		}
		if (newFile.exists()) {					// Ask the user whether to replace the file.
			NotifyDescriptor d = new NotifyDescriptor.Confirmation(
					"The file " + newFile.getName() + " already exists.\nDo you want to replace it?",
					"Overwrite File Check",
					NotifyDescriptor.YES_NO_OPTION,
					NotifyDescriptor.WARNING_MESSAGE);
			d.setValue(NotifyDescriptor.CANCEL_OPTION);
			Object result = DialogDisplayer.getDefault().notify(d);
			if (result != DialogDescriptor.YES_OPTION) {
				return;
			}
		}
		
		if (copy("IndexerFileTemplate.xml", newFile)) {		// copy the file
			StatusDisplayer.getDefault().setStatusText("Creating new file: " + newFile.getName());
			OpenFile.open(newFile);
		}
	}
	
	/**
	 * Copy the template with the given name to the new file.
	 * @param inStr	Name of template file
	 * @param outFile new file
	 * @return true=OK, false=problem
	 */
	private boolean copy(String inStr, File outFile) {
		InputStream inStream = getClass().getResourceAsStream(inStr);

		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(outFile);
		} catch (FileNotFoundException ex) {
			Exceptions.printStackTrace(ex);
			return false;
		}

		int c;
		try {	// yes, this is horrible. Java 7.0 has better ways of doing this.
			while ((c = inStream.read()) != -1) {
				outStream.write(c);
			}
		} catch (IOException ex) {
			Exceptions.printStackTrace(ex);
			return false;
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException ex) {
				Exceptions.printStackTrace(ex);
				return false;
			}
		}
		return true;
	}
}
