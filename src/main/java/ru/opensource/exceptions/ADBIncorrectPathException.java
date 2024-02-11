package ru.opensource.exceptions;

public class ADBIncorrectPathException extends Exception{

    public ADBIncorrectPathException(String errorMessage){
        super(errorMessage);
    }
}
