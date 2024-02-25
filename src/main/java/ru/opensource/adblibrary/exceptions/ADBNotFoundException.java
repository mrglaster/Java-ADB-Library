package ru.opensource.adblibrary.exceptions;

public class ADBNotFoundException extends ADBException{
    public ADBNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
