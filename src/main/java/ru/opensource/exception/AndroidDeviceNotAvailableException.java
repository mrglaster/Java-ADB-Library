package ru.opensource.exception;

public class AndroidDeviceNotAvailableException extends Exception{

    public AndroidDeviceNotAvailableException(String errorMessage){
        super(errorMessage);
    }
}
