package ru.opensource.exceptions;

public class ADBPermissionCollectingException extends Exception{
    public ADBPermissionCollectingException(String errorMessage){
        super(errorMessage);
    }
}
