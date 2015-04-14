
package com.billooms.indexwheel.api;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

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
public interface IndexWheelMgr {
	/** Property name used for adding an IndexWheel from the list */
	String PROP_ADD = "add";
	/** Property name used for removing an IndexWheel from the list */
	String PROP_REMOVE = "remove";
	/** Property name used for removing all IndexWheels from the list */
	String PROP_CLEAR = "clear";
	/** Property name used for reading a new list from an xml file */
	String PROP_READXML = "readXML";
	
	/**
	 * Get the number of wheels in the list.
	 * @return number of wheels
	 */
	int size();

	/**
	 * Get the IndexWheel with the index in the list. 
	 * It is not removed from the list. 
	 * @param n index in the list
	 * @return the IndexWheel (or null if the given index is out of range).
	 */
    IndexWheel get(int n);

	/**
	 * Get an unmodifiable list of all the IndexWheels. 
	 * Attempts to modify the returned list, whether direct or via its iterator, 
	 * result in an UnsupportedOperationException.
	 * @return unmodifiable list
	 */
    List<IndexWheel> getAll();
	
	/**
	 * Return the index of the given IndexWheel.
	 * @param wh IndexWheel
	 * @return index of the given Indexwheel (or -1 if it is not in the list)
	 */
	int indexOf(IndexWheel wh);

	/**
	 * Add a new IndexWheel to the list with default values.
	 * This fires a PROP_ADD property change with the new IndexWheel object. 
	 * @return the new IndexWheel
	 */
    IndexWheel addWheel();

	/**
	 * Remove the IndexWheel by index from the list.
	 * This fires a PROP_REMOVE property change with the old IndexWheel object, 
	 * and the new object is the first in the list (or null). 
	 * @param n index in the list
	 */
    void remove(int n);

	/**
	 * Remove the given IndexWheel from the list, 
	 * provided that it is contained in the list.
	 * This fires a PROP_REMOVE property change with the old IndexWheel object, 
	 * and the new object is the first in the list (or null).  
	 * @param wh IndexWheel
	 */
    void remove(IndexWheel wh);
	
	/**
	 * Clear the list.
	 * This fires a PROP_CLEAR property change.
	 */
	void clear();

	/**
	 * Write IndexWheel information to an xml file.
	 * @param file File for writing the xml
	 */
	void writeXML(File file);

	/**
	 * Read the "IndexWheels" data from the xml file.
	 * This fires a PROP_READXML property change with the name of the file.
	 * @param file The xml file to read
	 */
	void readXML(File file);

	/**
	 * Add the given PropertyChangeListener to this object.
	 * @param listener
	 */
    void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove the given PropertyChangeListener from this object.
	 * @param listener
	 */
    void removePropertyChangeListener(PropertyChangeListener listener);
	
}
