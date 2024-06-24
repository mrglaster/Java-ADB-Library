package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when an invalid package name is encountered.
 * Inherits from ADBException.
 */
public class InvalidPackageNameException extends ADBException {

    /**
     * Constructs a new InvalidPackageNameException with the specified error message.
     *
     * @param errorMessage The error message that describes why the package name is invalid.
     */
    public InvalidPackageNameException(String errorMessage) {
        super(errorMessage);
    }
}
