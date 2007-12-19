/**
 * Project Wonderland
 *
 * $RCSfile:$
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision:$
 * $Date:$
 * $State:$
 */
package org.jdesktop.wonderland.server;

import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import java.io.Serializable;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import org.jdesktop.wonderland.server.comms.WonderlandClientCommsProtocol;

/**
 * SGS Boot class for Wonderland
 */
public class WonderlandBoot implements AppListener, Serializable {
    
    private final static Logger logger = 
            Logger.getLogger(WonderlandBoot.class.getName());
    
    /**
     * Initialize the server
     * @param props properties to initialize with
     */
    public void initialize(Properties props) {
        logger.info("Initialize Wonderland Server");

        // initialize the context object
        WonderlandContext.intialize();
        
        // create default communications protocols
        WonderlandContext.getCommsManager().registerProtocol(
                new WonderlandClientCommsProtocol());
        
        // initialize plugins
        ServiceLoader<ServerPlugin> plugins =
                ServiceLoader.load(ServerPlugin.class);
        
        for (ServerPlugin plugin : plugins) {
            logger.info("Initializing plugin: " + plugin);
            plugin.initialize();
        }
    }
    
    /**
     * Called when a new client logs in.  This returns an initial 
     * ProtocolSessionListener, which will negotiate the protocol
     * to speak with the client.
     * @param session the session for the new user
     * @return a ProtocolSessionListener used to negotiate the
     * proper protocol for the client.
     */
    public ClientSessionListener loggedIn(ClientSession session) {
        return new ProtocolSessionListener(session);
    }
}   
