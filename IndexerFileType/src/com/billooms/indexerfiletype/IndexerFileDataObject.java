
package com.billooms.indexerfiletype;

import com.billooms.indexwheel.api.IndexChildren;
import com.billooms.indexwheel.api.IndexWheel;
import com.billooms.indexwheel.api.IndexWheelMgr;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 * DataObject for Indexer xml files
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public class IndexerFileDataObject extends MultiDataObject implements PropertyChangeListener {
	
	private InstanceContent ic = new InstanceContent();
	private Lookup lookup;
	private IndexWheelMgr idxMgr;
	private Saver saver = new Saver();
	private Opener opener = new Opener();
	
	/**
	 * Create a new IndexerFileDataObject
	 * @param pf the primary file object
	 * @param loader loader of this data object
	 * @throws DataObjectExistsException there already is an object for the primary file
	 * @throws IOException failed or interrupted I/O operations
	 */
	public IndexerFileDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
		super(pf, loader);
		
		this.lookup = new AbstractLookup(ic);
		this.idxMgr = Lookup.getDefault().lookup(IndexWheelMgr.class);
		
        enableSaveAction(false);
		enableOpenAction(true);		// this enables the file to be opened
	}

	/**
	 * Provides node that should represent this data object
	 * @return the node representation
	 */
	@Override
	protected Node createNodeDelegate() {
		return new DataNode(this, new IndexChildren(), getLookup());
	}

	/**
	 * Represents a context of the data object
	 * @return lookup representing this data object and its content
	 */
	@Override
	public Lookup getLookup() {
		return this.lookup;
	}

	/**
	 * Listen for PropertyChangeEvents
	 * @param evt event
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof IndexWheelMgr) {
			if (evt.getPropertyName().equals(IndexWheelMgr.PROP_READXML)) {
				return;		// Don't enable Save after reading in an xml file
			}
		} else if (evt.getSource() instanceof IndexWheel) {
			if (evt.getPropertyName().equals(IndexWheel.PROP_ROTATION)) {
				return;		// Don't enable Save for rotations (they are not saved)
			}
		}
        enableSaveAction(true);
	}
	
	/**
	 * Remove any Saver from lookup and mark this as unmodified. 
	 * Un-saved changes are not un-done. 
	 * The file is not modified.
	 * This is mainly used so that the "Save All..." action doesn't try 
	 * to save the changes later.
	 */
	public void forgetChanges() {
		enableSaveAction(false);
	}
	
	

	/**
	 * Enable the Save All... action by marking this as modified 
	 * and add a Saver SaveCookie to the lookup.
	 * @param enableSave true=need to save this
	 */
	private void enableSaveAction(boolean enableSave) {
		this.setModified(enableSave);
		if (enableSave) {
			ic.add(saver);
		} else {
			ic.remove(saver);
		}
	}

	/**
	 * Implementation of SaveCookie for saving the data.
	 */
	private class Saver implements SaveCookie {
		@Override
		public void save() throws IOException {
			idxMgr.writeXML(FileUtil.toFile(IndexerFileDataObject.this.getPrimaryFile()));
			enableSaveAction(false);
		}

	}
	
	/**
	 * Enable opening by adding a Opener OpenCookie to the lookup.
	 * @param enableOpen true=this can be opened
	 */
	private void enableOpenAction(boolean enableOpen) {
		if (enableOpen) {
			ic.add(opener);
		} else {
			ic.remove(opener);
		}
	}

	/**
	 * Implementation of OpenCookie for reading the data from the file.
	 */
	private class Opener implements OpenCookie {
		@Override
		public void open() {
			if (idxMgr != null) {
				idxMgr.readXML(FileUtil.toFile(getPrimaryFile()));	// read the xml file
				idxMgr.addPropertyChangeListener(IndexerFileDataObject.this);		// listen for changes in the manager
				for (IndexWheel wh : idxMgr.getAll()) {
					wh.addPropertyChangeListener(IndexerFileDataObject.this);		// and each of the wheels
				}
			}
//			enableOpenAction(false);		// not sure why you would want this here
		}
	}
}
