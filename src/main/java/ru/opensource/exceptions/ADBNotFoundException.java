package ru.opensource.exceptions;

public class ADBNotFoundException extends Exception{
    public ADBNotFoundException(String errorMessage){
        super(errorMessage);
    }
}
