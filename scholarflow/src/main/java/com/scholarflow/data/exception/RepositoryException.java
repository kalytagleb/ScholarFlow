package com.scholarflow.data.exception;

import java.rmi.server.RMIClassLoaderSpi;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepositoryException(String message, Object... args) {
        super(String.format(message, args));
    }
}
