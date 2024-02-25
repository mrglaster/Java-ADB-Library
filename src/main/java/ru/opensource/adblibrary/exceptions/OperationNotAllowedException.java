package ru.opensource.adblibrary.exceptions;

public class OperationNotAllowedException extends ADBException{
    public OperationNotAllowedException(String errorMessage){
        super(errorMessage);
    }
}
