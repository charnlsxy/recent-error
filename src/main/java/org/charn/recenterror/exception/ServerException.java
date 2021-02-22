package org.charn.recenterror.exception;

public class ServerException extends RuntimeException {
    public ServerException(Throwable e) {
        super(e.getMessage());
    }

}
