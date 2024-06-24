package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when there is an error during execution of ADB shell commands.
 * Inherits from ADBException.
 */
public class ADBShellExecutionException extends ADBException {

    /**
     * Constructs a new ADBShellExecutionException with the specified error message.
     *
     * @param errorMessage The error message that describes the exception.
     */
    public ADBShellExecutionException(String errorMessage) {
        super(errorMessage);
    }
}
