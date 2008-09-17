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
package org.jdesktop.wonderland.testharness.slave.client3D;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveListener;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveSource;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.testharness.common.Client3DRequest;
import org.jdesktop.wonderland.testharness.common.LoginRequest;
import org.jdesktop.wonderland.testharness.common.TestRequest;

/**
 * A test client that simulates a 3D client
 */
public class Client3DSim
        implements SessionStatusListener
{
    /** a logger */
    private static final Logger logger = 
            Logger.getLogger(Client3DSim.class.getName());
    
    /** the name of this client */
    private String name;
    
    /** the mover thread */
    private UserSimulator userSim;
    
    private MessageTimer messageTimer = new MessageTimer();
    
    public Client3DSim(LoginRequest loginRequest) throws LoginFailureException {
        this.name = loginRequest.getUsername();
        
        WonderlandServerInfo server = new WonderlandServerInfo(loginRequest.getSgsServerName(), 
                                                               loginRequest.getSgsServerPort());
        LoginParameters login = new LoginParameters(name, loginRequest.getPasswd());
                
        // login
        CellClientSession session = new CellClientSession(server);
        session.addSessionStatusListener(this);
        session.login(login);
        
        logger.info(getName() + " login succeeded");
        
        final LocalAvatar avatar = session.getLocalAvatar();
        
        avatar.addViewCellConfiguredListener(new LocalAvatar.ViewCellConfiguredListener() {
            public void viewConfigured(LocalAvatar localAvatar) {
                ((MovableComponent)avatar.getViewCell().getComponent(MovableComponent.class)).addServerCellMoveListener(messageTimer);
                
                // Start the userSim once the view is fully configured
                userSim.start();
            }
        });
                      
        userSim = new UserSimulator(avatar);        
    }
    
    public void processRequest(TestRequest request) {
        if (request instanceof Client3DRequest) {
            processClient3DRequest((Client3DRequest)request);
        } else {
            Logger.getAnonymousLogger().severe("Unsupported request "+request.getClass().getName());
        }
    }
    
    private void processClient3DRequest(Client3DRequest request) {
        switch(request.getAction()) {
            case WALK :
                userSim.walkLoop(request.getDesiredLocations(), new Vector3f(1f,0f,0f), request.getSpeed(), request.getLoopCount());
                break;
            default :
                Logger.getAnonymousLogger().severe("Unsupported Client3DRequest "+request.getAction());
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void sessionStatusChanged(WonderlandSession session, 
                                     Status status)
    {
        logger.info(getName() + " change session status: " + status);
        if (status == Status.DISCONNECTED  && userSim != null) {
            userSim.quit();
        }
    }
    
    public void waitForFinish() throws InterruptedException {
        if (userSim == null) {
            return;
        }
        
        // wait for the thread to end
        userSim.join();
    }
        
    /**
     * A very basic UserSimulator, this really needs a lot of attention.....
     */
    class UserSimulator extends Thread {
        private Vector3f currentLocation = new Vector3f();
        private Vector3f[] desiredLocations;
        private int locationIndex;
        private Vector3f step = null;
        
        private float speed;
        
        private Quaternion orientation = null;
        private LocalAvatar avatar;
        private boolean quit = false;
        private boolean walking = false;
        private long sleepTime = 200; // Time between steps (in ms)
        private int currentLoopCount = 0;
        private int desiredLoopCount;
        
        private Semaphore semaphore;
                
        public UserSimulator(LocalAvatar avatar) {
            super("UserSimulator");
            this.avatar = avatar;
            semaphore = new Semaphore(0);
        }

        public synchronized boolean isQuit() {
            return quit;
        }
        
        public synchronized void quit() {
            this.quit = true;
        }
        
        @Override
        public void run() {
            // Set initial position
            avatar.localMoveRequest(currentLocation, orientation); 
            
            while(!quit) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client3DSim.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                while(!quit && walking) {
                    if (currentLocation.subtract(desiredLocations[locationIndex]).lengthSquared()<0.1) {   // Need epsilonEquals
                        if (locationIndex<desiredLocations.length-1) {
                            locationIndex++;

                            step = desiredLocations[locationIndex].subtract(currentLocation);
                            step.multLocal(speed/(1000f/sleepTime));

                        } else if (locationIndex==desiredLocations.length-1 && desiredLoopCount!=currentLoopCount) {
                            currentLoopCount++;
                            locationIndex = 0;

                            step = desiredLocations[locationIndex].subtract(currentLocation);
                            step.multLocal(speed/(1000f/sleepTime));
                        } else  
                            walking = false;
                    }
                    
                    if (walking) {
                        currentLocation.addLocal(step);
                        avatar.localMoveRequest(currentLocation, orientation);    
                        messageTimer.messageSent(new CellTransform(orientation, currentLocation));
                    }
                    
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client3DSim.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
        /**
         * Walk  from the current location to the new location specified, and
         * orient to the give look direction
         * @param location
         * @param lookDirection
         * @param speed in meters/second
         */
        void walkLoop(Vector3f[] locations, Vector3f lookDirection, float speed, int loopCount) {
            this.speed = speed;
            locationIndex = 0;
            desiredLocations = locations;
            desiredLoopCount = loopCount;
            currentLoopCount = 0;
            
            step = new Vector3f(desiredLocations[0]);
            step.subtractLocal(currentLocation);
            step.multLocal(speed/(1000f/sleepTime));
            
            walking = true;
            semaphore.release();
        }
        
        /**
         * Send audio data to the server
         * 
         * TODO implement
         */
        public void talk() {
            
        }
    }
    
    /**
     * Measure the time between us sending a move request to the server and the server 
     * sending the message back to us.
     */
    class MessageTimer implements CellMoveListener {
        
        private long timeSum=0;
        private long lastReport;
        private int count = 0;
        private long min = Long.MAX_VALUE;
        private long max = 0;
        
        private static final long REPORT_INTERVAL = 5000; // Report time in ms
        
        private LinkedList<MessageTime> messageTimes = new LinkedList();
        
        public MessageTimer() {
            lastReport = System.nanoTime();
        }

        /**
         * Callback for messages from server
         * @param arg0
         * @param arg1
         */
        public void cellMoved(CellTransform transform, CellMoveSource arg1) {
            long sendTime;
            
            MessageTime mt=null;
            synchronized(messageTimes) {
                mt = messageTimes.getFirst();
                if (!mt.cellTransform.equals(transform)) {
//                    System.out.println("Transform does not match, no time available");
                    // Handle messages from the server, which were not originated on this client
                    return;
                }
                messageTimes.removeFirst();
            }
            
            sendTime = mt.sendTime;

            long time = ((System.nanoTime())-sendTime)/1000000;

            min = Math.min(min, time);
            max = Math.max(max, time);
            timeSum += time;
            count++;

            if (System.nanoTime()-lastReport > REPORT_INTERVAL*1000000) {
                long avg = timeSum/count;
                logger.info("Roundtrip time avg "+avg + "ms "+name+" min "+min+" max "+max);
                timeSum=0;
                lastReport = System.nanoTime();
                count = 0;
                min = Long.MAX_VALUE;
                max = 0;            
            }
        }
        
        public void messageSent(CellTransform transform) {
            synchronized(messageTimes) {
                messageTimes.add(new MessageTime(transform, System.nanoTime()));
            }
        }
    }
    
    class MessageTime {
        public CellTransform cellTransform;
        public long sendTime;
        
        public MessageTime(CellTransform cellTransformIn, long sendTime) {
            cellTransform = cellTransformIn.clone(null);
            this.sendTime = sendTime;
        }

    }
}
