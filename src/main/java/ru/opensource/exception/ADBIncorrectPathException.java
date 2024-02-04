package ru.opensource.exception;

public class ADBIncorrectPathException extends Exception{

    public ADBIncorrectPathException(String errorMessage){
        super(errorMessage);
    }
}
