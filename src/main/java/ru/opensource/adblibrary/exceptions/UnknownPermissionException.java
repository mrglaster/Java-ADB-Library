package ru.opensource.adblibrary.exceptions;

public class UnknownPermissionException extends ADBException{
    public UnknownPermissionException(String errorMessage){
        super(errorMessage);
    }
}
