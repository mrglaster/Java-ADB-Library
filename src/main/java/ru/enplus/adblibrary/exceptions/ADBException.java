package ru.enplus.adblibrary.exceptions;

/**
 * A general exception class for handling errors related to ADB (Android Debug Bridge) operations.
 */
public class ADBException extends Exception {

    /**
     * Constructs a new ADBException with the specified error message.
     *
     * @param errorMessage The error message that describes the exception.
     */
    public ADBException(String errorMessage) {
        super(errorMessage);
    }
}
