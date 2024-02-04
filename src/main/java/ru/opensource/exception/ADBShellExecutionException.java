package ru.opensource.exception;

public class ADBShellExecutionException extends Exception{
    public ADBShellExecutionException(String errorMessage){
        super(errorMessage);
    }
}
