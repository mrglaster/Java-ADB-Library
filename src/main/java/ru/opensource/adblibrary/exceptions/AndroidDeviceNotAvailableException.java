package ru.opensource.adblibrary.exceptions;

public class AndroidDeviceNotAvailableException extends ADBException{

    public AndroidDeviceNotAvailableException(String errorMessage){
        super(errorMessage);
    }
}
