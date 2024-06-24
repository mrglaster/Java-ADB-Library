package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when an Android device is not available for connection or operation.
 * Inherits from ADBException.
 */
public class AndroidDeviceNotAvailableException extends ADBException {

    /**
     * Constructs a new AndroidDeviceNotAvailableException with the specified error message.
     *
     * @param errorMessage The error message that describes why the Android device is not available.
     */
    public AndroidDeviceNotAvailableException(String errorMessage) {
        super(errorMessage);
    }
}
