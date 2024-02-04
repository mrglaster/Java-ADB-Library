package ru.opensource.exception;

public class ADBNotFoundException extends Exception{
    public ADBNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
