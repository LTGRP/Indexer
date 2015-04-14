
package com.billooms.indexwheel;

import com.billooms.indexwheel.api.IndexWheel;
import com.billooms.indexwheel.api.IndexWheelMgr;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.lookup.ServiceProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Manage a list of IndexWheels that are used together on a lathe.
 * @author Bill Ooms. Copyright 2011 Studio of Bill Ooms. All rights reserved.
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
@ServiceProvider(service = IndexWheelMgr.class)
public class IndexWheelMgrImpl implements IndexWheelMgr, PropertyChangeListener {

	private final ResourceBundle resBundle = ResourceBundle.getBundle("com/billooms/indexwheel/Bundle");
    private List<IndexWheel> wheelList;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	/** 
	 * Create a list of IndexWheels.
	 */
	public IndexWheelMgrImpl() {
        this.wheelList = new ArrayList<IndexWheel>();
	}

	/**
	 * Listen for a PROP_ROTATION PropertyChange in any of the IndexWheels 
	 * and update all the other wheels in the list.
	 * @param evt 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(IndexWheel.PROP_ROTATION)) {
			for (IndexWheel wh : wheelList) {
				((IndexWheelImpl)wh).updateWheel();	// when rotation changes, update all wheels
			}
		}
	}
	
	/**
	 * Get the number of wheels in the list.
	 * @return number of wheels
	 */
	@Override
	public int size() {
		return wheelList.size();
	}

	/**
	 * Get the IndexWheel with the index in the list. 
	 * It is not removed from the list. 
	 * @param n index in the list
	 * @return the IndexWheel (or null if the given index is out of range).
	 */
	@Override
    public IndexWheel get(int n) {
		if ((n < 0) || (n >= wheelList.size())) {
			return null;
		}
		return wheelList.get(n);
    }

	/**
	 * Get an unmodifiable list of all the IndexWheels. 
	 * Attempts to modify the returned list, whether direct or via its iterator, 
	 * result in an UnsupportedOperationException.
	 * @return unmodifiable list
	 */
	@Override
    public List<IndexWheel> getAll() {
        return Collections.unmodifiableList(this.wheelList);
    }
	
	/**
	 * Return the index of the given IndexWheel.
	 * @param wh IndexWheel
	 * @return index of the given Indexwheel (or -1 if it is not in the list)
	 */
	@Override
	public int indexOf(IndexWheel wh) {
		return wheelList.indexOf(wh);
	}

	/**
	 * Add a new IndexWheel to the list with default values.
	 * This fires a PROP_ADD property change with the new IndexWheel object. 
	 * @return the new IndexWheel
	 */
	@Override
    public synchronized IndexWheel addWheel() {
        IndexWheel wheel = new IndexWheelImpl();
        this.wheelList.add(wheel);
		wheel.addPropertyChangeListener(this);	// listen to the wheel for changes
        this.pcs.firePropertyChange(PROP_ADD, null, wheel);
        return wheel;
    }

	/**
	 * Remove the IndexWheel by index from the list.
	 * This fires a PROP_REMOVE property change with the old IndexWheel object, 
	 * and the new object is the first in the list (or null). 
	 * @param n index in the list
	 */
	@Override
    public synchronized void remove(int n) {
		if ((n < 0) || (n >= wheelList.size())) {
			return;
		}
		IndexWheel old = wheelList.get(n);
		wheelList.remove(n);
		this.pcs.firePropertyChange(PROP_REMOVE, old, get(0));
    }

	/**
	 * Remove the given IndexWheel from the list, 
	 * provided that it is contained in the list.
	 * This fires a PROP_REMOVE property change with the old IndexWheel object, 
	 * and the new object is the first in the list (or null). 
	 * @param wh IndexWheel
	 */
	@Override
    public synchronized void remove(IndexWheel wh) {
		if (wheelList.contains(wh)) {
			wheelList.remove(wh);
			this.pcs.firePropertyChange(PROP_REMOVE, wh, get(0));
		}
    }
	
	/**
	 * Clear the list.
	 * This fires a PROP_CLEAR property change.
	 */
	@Override
	public synchronized void clear() {
		wheelList.clear();
		this.pcs.firePropertyChange(PROP_CLEAR, null, null);
	}

	/**
	 * Write IndexWheel information to an xml file.
	 * @param file File for writing the xml
	 */
	@Override
	public void writeXML(File file) {
		PrintWriter out;
		try {
			out = new PrintWriter(new FileOutputStream(file));
		} catch (Exception e) {
			NotifyDescriptor d = new NotifyDescriptor.Message(
					"Error while trying to open the file:\n" + e,
					NotifyDescriptor.ERROR_MESSAGE);
			DialogDisplayer.getDefault().notify(d);
			return;
		}
		
        try {
            out.println("<?xml version=\"1.0\"?>");
            out.println("<!--");        // *** Remove this later???
			out.println("<!DOCTYPE " + 
					resBundle.getString("XML_Type") +
					" PUBLIC " + 
					resBundle.getString("DTD_IPL") +
					" " +
					resBundle.getString("DTD_URL") +
					">");
            out.println("-->");        // *** Remove this later???
            out.println("<" +
					resBundle.getString("XML_Type") +
					" version=\"" +
					(resBundle.getString("XML_Version")).substring(0, 3) +
					"\"" + ">");	// only 3 characters for version

			for (IndexWheel w : wheelList) {
				w.writeXML(out);
			}
		
            out.println("</" +
					resBundle.getString("XML_Type") +
					">");
		} catch (Exception e) {
			NotifyDescriptor d = new NotifyDescriptor.Message("Error while trying to write the file:\n" + e,
					NotifyDescriptor.ERROR_MESSAGE);
			DialogDisplayer.getDefault().notify(d);
        } finally {
			out.close();
		}
	}

	/**
	 * Read the "IndexWheels" data from the xml file.
	 * This fires a PROP_READXML property change with the name of the file.
	 * @param file The xml file to read
	 */
	@Override
	public void readXML(File file) {
        Document xmldoc;

        try {
            xmldoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (Exception e) {
			NotifyDescriptor d = new NotifyDescriptor.Message(
					"Error while trying to read/parse the file:\n" + e,
					NotifyDescriptor.ERROR_MESSAGE);
			DialogDisplayer.getDefault().notify(d);
            return;
        }
		
        try {
            Element rootElement = xmldoc.getDocumentElement();
            String version = "99.0";	// make this high, so error if not found in file
            if (!rootElement.getNodeName().equals(resBundle.getString("XML_Type"))) {
                throw new Exception("File is not a " + resBundle.getString("XML_Type") + " file.");
            }
			version = rootElement.getAttribute("version");
			if (Double.parseDouble(version) > Double.parseDouble(resBundle.getString("XML_Version"))) {
				throw new Exception("File was written with a newer version of " + resBundle.getString("XML_Type"));
			}

			wheelList.clear();		// discard prior 
			NodeList list = rootElement.getElementsByTagName("IndexWheel");	// find all <IndexWheel> at the root level
			for (int i = 0; i < list.getLength(); i++) {
				IndexWheel wheel = new IndexWheelImpl((Element)list.item(i));
				wheelList.add(wheel);					// add to the list
				wheel.addPropertyChangeListener(this);	// listen to the wheel for changes
			}
        } catch (Exception e) {
			NotifyDescriptor d = new NotifyDescriptor.Message("Error while trying to read the xml data:\n" + e,
					NotifyDescriptor.ERROR_MESSAGE);
			DialogDisplayer.getDefault().notify(d);
        }
		this.pcs.firePropertyChange(PROP_READXML, null, file.getName());
	}

	/**
	 * Add the given PropertyChangeListener to this object.
	 * @param listener
	 */
	@Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

	/**
	 * Remove the given PropertyChangeListener from this object.
	 * @param listener
	 */
	@Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
	
}
