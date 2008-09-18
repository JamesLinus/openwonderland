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
package org.jdesktop.wonderland.client.app.utils.clientSocket;

public class SlaveClientSocket extends ClientSocket {

    public MasterClientSocket (BigInteger clientId, Socket s, ClientSocketListener listener) {
	super(clientId, s, listener);
	master = false;

	// This is not enabled until the master client has sent all of the welcome message contents.
	setEnable(false);

	if (ENABLE_STATS) {
	    statReporter = new StatisticsReporter(30, /* secs */
						  new ReadStatistics(), 
						  new ReadStatistics(),
						  new ReadStatistics());
	    statReporter.start();
	}
    }

    public void initialize () {
	if (slaveSocket.initialize()) {
	    slaveSocket.start();
	}
    }

    public void close () {
	super.close();
	if (ENABLE_STATS) {
	    statReporter.stop();
	}
    }
}