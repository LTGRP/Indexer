
package com.billooms.indexwheel.api;

import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 * This is a Node wrapper for the root node so that we can add icons
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
 */
public class RootNode extends AbstractNode {
	
    
	/**
	 * Create a root node with the given children
	 * @param children children for this root node
	 */
	public RootNode(Children children) {
        super(children);
		this.setName("IndexWheels");
        this.setDisplayName("IndexWheels");
    }
    
	/**
	 * Set the icon for this node
	 * @param type constants from BeanInfo
	 * @return icon to use
	 */
	@Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage ("com/billooms/indexwheel/api/right-rectangle.png");
    }
    
	/**
	 * Set the icon for this node when it is open
	 * @param type constants from BeanInfo
	 * @return icon to use
	 */
	@Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.loadImage ("com/billooms/indexwheel/api/down-rectangle.png");
    }
}
