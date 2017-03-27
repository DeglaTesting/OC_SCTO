/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rdcit.tools;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author sa841
 */
public class Log {

    public static Logger LOGGER =  Logger.getLogger(Log.class.getName());

    static{
        String log4jConfPath = Log.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);
    }

}
