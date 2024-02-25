package ru.opensource.adblibrary.exceptions;

public class InvalidPackageNameException extends ADBException{
    public InvalidPackageNameException(String errorMessage){
        super(errorMessage);
    }
}
