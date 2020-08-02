package io.giremock.server;

public class RunnerException extends RuntimeException {

    public RunnerException(String message, Exception e) {
        super(message, e);
    }
}
