/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.server.cell;

import java.io.Serializable;

/**
 * Listener for tracking changes to the list of components on a cellMO
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface ComponentChangeListenerSrv extends Serializable {

    /**
     * The type of change: adding or removing
     */
    public enum ChangeType {
        /**
         * A component was added to the cell
         */
        ADDED,

        /**
         * A component was removed from the cell
         */
        REMOVED
    };

    /**
     * Called when the cells component list has changed
     * 
     * @param cell the cells whose component list has changed
     * @param changeSource the source or originator of the change
     * @param component The component in question
     */
    public void componentChanged(CellMO cell, ChangeType type,
                                 CellComponentMO component);
}
