package io.giremock.server;

import java.net.MalformedURLException;

public class ServiceLoaderException extends RuntimeException {

    public ServiceLoaderException(String message, Exception e) {
        super(message, e);
    }
}
