package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when an unknown permission is encountered.
 * Inherits from ADBException.
 */
public class UnknownPermissionException extends ADBException {

    /**
     * Constructs a new UnknownPermissionException with the specified error message.
     *
     * @param errorMessage The error message that describes the unknown permission encountered.
     */
    public UnknownPermissionException(String errorMessage) {
        super(errorMessage);
    }
}
