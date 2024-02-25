package ru.opensource.adblibrary.exceptions;

public class ADBPermissionCollectingException extends ADBException{
    public ADBPermissionCollectingException(String errorMessage){
        super(errorMessage);
    }
}
