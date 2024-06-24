package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when an unknown package is encountered.
 * Inherits from ADBException.
 */
public class UnknownPackageException extends ADBException {

    /**
     * Constructs a new UnknownPackageException with the specified error message.
     *
     * @param errorMessage The error message that describes the unknown package encountered.
     */
    public UnknownPackageException(String errorMessage) {
        super(errorMessage);
    }
}
