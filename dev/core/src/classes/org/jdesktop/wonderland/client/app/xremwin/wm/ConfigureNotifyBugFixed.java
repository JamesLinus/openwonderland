/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State$
 */
package org.jdesktop.wonderland.client.app.xremwin.wm;

import gnu.x11.Display;
import gnu.x11.event.ConfigureNotify;

/**
 *
 * @author deronj
 */

@StableAPI
public class ConfigureNotifyBugFixed extends ConfigureNotify 
{
    public ConfigureNotifyBugFixed (Display dpy, byte[] data) {
	super(dpy, data); 
    }

    public int x () { 
	int x = read2(16); 
	// Be sure to sign extend
	return (x<<16)>>16;
    }

    public int y () { 
	int y = read2(18); 
	// Be sure to sign extend
	return (y<<16)>>16;
    }
}
