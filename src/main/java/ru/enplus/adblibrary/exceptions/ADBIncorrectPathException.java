package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when an incorrect ADB (Android Debug Bridge) path is provided.
 * Inherits from ADBException.
 */
public class ADBIncorrectPathException extends ADBException {

    /**
     * Constructs a new ADBIncorrectPathException with the specified error message.
     *
     * @param errorMessage The error message that describes the exception.
     */
    public ADBIncorrectPathException(String errorMessage) {
        super(errorMessage);
    }
}
