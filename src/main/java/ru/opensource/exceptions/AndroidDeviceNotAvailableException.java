package ru.opensource.exceptions;

public class AndroidDeviceNotAvailableException extends Exception{

    public AndroidDeviceNotAvailableException(String errorMessage){
        super(errorMessage);
    }
}
