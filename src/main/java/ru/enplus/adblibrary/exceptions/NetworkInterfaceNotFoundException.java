package ru.enplus.adblibrary.exceptions;

/**
 * Exception thrown when a network interface is not found.
 * Inherits from ADBException.
 */
public class NetworkInterfaceNotFoundException extends ADBException {

    /**
     * Constructs a new NetworkInterfaceNotFoundException with the specified error message.
     *
     * @param errorMessage The error message that describes why the network interface was not found.
     */
    public NetworkInterfaceNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
