/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.integration;

import static org.epics.pvmanager.ExpressionLanguage.*;
import org.epics.pvmanager.PVManager;
import org.diirt.util.time.TimeDuration;
import org.diirt.util.time.Timestamp;
import org.epics.vtype.AlarmSeverity;
import static org.epics.pvmanager.integration.VTypeMatchMask.*;
import org.epics.pvmanager.jca.JCADataSource;
import static org.epics.vtype.ValueFactory.*;
import static org.epics.pvmanager.integration.Constants.*;
import org.epics.vtype.VDouble;
import org.epics.vtype.VString;

/**
 * Superclass for Channel Access tests.
 *
 * @author carcassi
 */
public abstract class AbstractCATestPhase extends TestPhase {

    protected void init(String iocName) {
        if (getDebugLevel() >= 1) {
            System.out.println("Initializing IOC " + iocName);
        }
        
        // Open command and output
        addReader(PVManager.read(channel("command")), TimeDuration.ofHertz(50));
        addWriter("command", PVManager.write(channel("command")));
        addReader(PVManager.read(channel("output")), TimeDuration.ofHertz(50));
        pause(1000);
        
        // Reset ioc to known state
        restart(iocName);
    }
    
    protected void restart(String iocName) {
        if (getDebugLevel() >= 1) {
            System.out.println("Restart to IOC " + iocName);
        }
        
        pause(500);
        write("command", "start " + iocName + " 1");
        pause(500);
        waitFor("command", "ready", 20000);
    }
    
    protected void pauseNetwork(int secPause) {
        if (getDebugLevel() >= 1) {
            System.out.println("Pause network for " + secPause + " sec");
        }
        
        pause(500);
        write("command", "netpause " + secPause);
        pause(500);
        waitFor("command", "ready", (secPause + 20) * 1000);
    }
    
    protected void channelConnections(String channelName, int expected) {
        if (getDebugLevel() >= 1) {
            System.out.println("Querying channel connections for '" + channelName + "'");
        }
        
        pause(500);
        write("command", "connections " + channelName);
        pause(500);
        waitFor("command", "ready", 15000);
        VString output = (VString) valueFor("output");
        int actual = Integer.valueOf(output.getValue());
        // FIXME: the comparison should actually not be done here!
        if (actual != expected) {
            throw new RuntimeException("Expected " + expected + " connections on '" + channelName + "' (was " + actual + ")");
        }
    }
    
    protected boolean isDbePropertySupported() {
        JCADataSource dataSource =  (JCADataSource) PVManager.getDefaultDataSource();
        return dataSource.isDbePropertySupported();
    }
    
    protected void singleChannelConnection(String channelName) {
        if (isDbePropertySupported()) {
            channelConnections(channelName, 2);
        } else {
            channelConnections(channelName, 1);
        }
    }

}