package ru.opensource.adblibrary.exceptions;

public class UnknownPackageException extends ADBException{
    public UnknownPackageException(String errorMessage){
        super(errorMessage);
    }
}
