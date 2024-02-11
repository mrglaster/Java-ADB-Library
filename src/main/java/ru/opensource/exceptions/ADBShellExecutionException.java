package ru.opensource.exceptions;

public class ADBShellExecutionException extends Exception{
    public ADBShellExecutionException(String errorMessage){
        super(errorMessage);
    }
}
