package io.giremock.protoc.helper;

public class HelperException extends RuntimeException {

    public HelperException(String message, Exception exception){
        super(message, exception);
    }

}
