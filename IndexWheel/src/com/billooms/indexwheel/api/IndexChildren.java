
package com.billooms.indexwheel.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * Implements an array of child nodes which are IndexWheels
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public class IndexChildren extends Children.Keys<IndexWheel> implements PropertyChangeListener {

    private IndexWheelMgr mgr;
	
	/**
	 * Implements an array of child nodes associated nonuniquely with keys and sorted by these keys.
	 */
	public IndexChildren() {
        this.mgr = Lookup.getDefault().lookup(IndexWheelMgr.class);
        if (this.mgr != null) {
            this.mgr.addPropertyChangeListener(this);	// listen for additions/subtractions
        }
	}
	
	
	/**
	 * Create nodes for a given key.
	 * @param key the key
	 * @return array of Nodes
	 */
	@Override
	protected Node[] createNodes(IndexWheel key) {
        return new Node[]{new IndexWheelNode(key)};
	}

	/**
	 * Called when children are first asked for nodes.
	 */
	@Override
    protected void addNotify() {
        super.addNotify();
        if (null != this.mgr) {
            this.setKeys(this.mgr.getAll());
        }
    }

	/**
	 * Listen for property changes and generate a new set of keys.
	 * @param evt event
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof IndexWheelMgr) {
			this.setKeys(this.mgr.getAll());	// IndexWheelMgr has added, removed, or cleared
        }
	}
}
