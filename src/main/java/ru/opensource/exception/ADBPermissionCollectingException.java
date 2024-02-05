package ru.opensource.exception;

public class ADBPermissionCollectingException extends Exception{
    public ADBPermissionCollectingException(String errorMessage){
        super(errorMessage);
    }
}
