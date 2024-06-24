package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when an operation is not allowed.
 * Inherits from ADBException.
 */
public class OperationNotAllowedException extends ADBException {

    /**
     * Constructs a new OperationNotAllowedException with the specified error message.
     *
     * @param errorMessage The error message that describes why the operation is not allowed.
     */
    public OperationNotAllowedException(String errorMessage) {
        super(errorMessage);
    }
}
