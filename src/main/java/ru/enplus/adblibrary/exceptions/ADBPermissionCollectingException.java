package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when there is an error while collecting permissions for an Android application using ADB.
 * Inherits from ADBException.
 */
public class ADBPermissionCollectingException extends ADBException {

    /**
     * Constructs a new ADBPermissionCollectingException with the specified error message.
     *
     * @param errorMessage The error message that describes the exception.
     */
    public ADBPermissionCollectingException(String errorMessage) {
        super(errorMessage);
    }
}
