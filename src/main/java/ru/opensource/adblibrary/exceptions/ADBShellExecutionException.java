package ru.opensource.adblibrary.exceptions;

public class ADBShellExecutionException extends ADBException{
    public ADBShellExecutionException(String errorMessage){
        super(errorMessage);
    }
}
