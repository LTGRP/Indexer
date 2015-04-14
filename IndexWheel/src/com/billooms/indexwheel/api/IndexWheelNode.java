
package com.billooms.indexwheel.api;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.ErrorManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * This is a Node wrapped around an IndexWheel to provide property editing, tree viewing, etc.
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public class IndexWheelNode extends AbstractNode {

	/**
	 * Create a new IndexWheelNode for the given IndexWheel
	 * @param wh an IndexWheel
	 */
    public IndexWheelNode(IndexWheel wh) {
        super(Children.LEAF, Lookups.singleton(wh));	// there will be no children
		this.setName(wh.getID());
        this.setDisplayName(wh.getName());
		this.setIconBaseWithExtension("com/billooms/indexwheel/api/icon16.png");
		
		wh.addPropertyChangeListener(new PropertyChangeListener() {	// listen for name to change
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				if (pce.getPropertyName().equals(IndexWheel.PROP_NAME)) {
					setDisplayName((String)pce.getNewValue());		// update the display
				}
			}
		});
    }
	
	/**
	 * Add my own actions for the node.
	 * @param popup Find actions for context meaning or for the node itself.
	 * @return List of all actions
	 */
	@Override
	public Action[] getActions(boolean popup) {
		Action[] defaults = super.getActions(popup);	// the default actions includes "Properties"
		Action[] newActions = new Action[defaults.length + 1];
		newActions[0] = new DeleteAction();
		System.arraycopy(defaults, 0, newActions, 1, defaults.length);
		return newActions;
	}
	
    /**
     * Initialize a property sheet
     * @return property sheet
     */
	@Override
	protected Sheet createSheet() {
		IndexWheel wh = getLookup().lookup(IndexWheel.class);
		Sheet sheet = Sheet.createDefault();
		Sheet.Set set = Sheet.createPropertiesSet();
		set.setDisplayName("IndexWheel Properties");
		try {
//			Property idProp = new PropertySupport.Reflection<String>(wh, String.class, "getID", null);
//			idProp.setName("id");
//			idProp.setShortDescription("uniique ID");
//			set.put(idProp);
			
			Property nameProp = new PropertySupport.Reflection<String>(wh, String.class, "name");
			nameProp.setName("name");
			nameProp.setShortDescription("name");
			set.put(nameProp);
			
			Property nHolesProp = new PropertySupport.Reflection(wh, int.class, "numHoles");
			nHolesProp.setName("numHoles");
			nHolesProp.setShortDescription("number of holes");
			set.put(nHolesProp);
			
			Property phaseProp = new PropertySupport.Reflection(wh, double.class, "phase");
			phaseProp.setName("phase");
			phaseProp.setShortDescription("phase (range 0.0 to 1.0)");
			set.put(phaseProp);
			
			Property rotateProp = new PropertySupport.Reflection(wh, double.class, "getRotation", null);
			rotateProp.setName("rotation");
			rotateProp.setShortDescription("rotation of the wheel (shared by all wheels)");
			set.put(rotateProp);
		} catch (NoSuchMethodException ex) {
			ErrorManager.getDefault();
		}
		sheet.put(set);
		return sheet;
	}
	
	
	/**
	 * Nested inner class for action deleting an IndexWheel.
	 */
	private class DeleteAction extends AbstractAction {

		/**
		 * Create the DeleteAction
		 */
		public DeleteAction() {
			putValue(NAME, "Delete");
		}

		/**
		 * Remove the selected IndexWheel. 
		 * Don't permit the last one to be deleted.
		 * @param e 
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			IndexWheel wh = getLookup().lookup(IndexWheel.class);
			IndexWheelMgr mgr = Lookup.getDefault().lookup(IndexWheelMgr.class);
			if (mgr.size() == 1) {	// don't allow removing the last one
				return;
			}
			mgr.remove(wh);
		}
	}
}
