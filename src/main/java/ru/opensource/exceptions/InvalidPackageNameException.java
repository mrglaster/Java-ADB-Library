package ru.opensource.exceptions;

public class InvalidPackageNameException extends Exception{
    public InvalidPackageNameException(String errorMessage){
        super(errorMessage);
    }
}
