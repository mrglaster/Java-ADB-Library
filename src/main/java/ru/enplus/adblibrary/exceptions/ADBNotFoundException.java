package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when the ADB (Android Debug Bridge) tool is not found in the system path.
 * Inherits from ADBException.
 */
public class ADBNotFoundException extends ADBException {

    /**
     * Constructs a new ADBNotFoundException with the specified error message.
     *
     * @param errorMessage The error message that describes the exception.
     */
    public ADBNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
