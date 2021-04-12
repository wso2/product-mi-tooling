package org.wso2.ei.dashboard.core.exception;

/**
 * RuntimeException to throw when exception/error is caught inside dashboard server due to error responses from the
 * MI management API invocations.
 */
public class ManagementApiException extends Exception {

    private int errorCode = 500;

    public ManagementApiException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ManagementApiException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ManagementApiException(int errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
