package ru.opensource.logger;

import java.util.logging.Logger;

public class ADBServiceLogger extends Logger {
    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * 
     */
    public ADBServiceLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }
    
    public void logInfo(String message){
        this.info(message);
    }
    
    public void logWarning(String message){
        this.warning(message);
    }
}
