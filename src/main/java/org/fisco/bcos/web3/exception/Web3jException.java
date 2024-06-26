package org.fisco.bcos.web3.exception;

public class Web3jException extends Exception {
    public static class ErrorCode {
        private ErrorCode() {}

        public static final int OK = 0;
        public static final int CONFIG_ERROR = 7000;
        public static final int RESOURCE_NOT_FOUND = 7001;
        public static final int RESOURCE_METHOD_NOT_FOUND = 7002;
        public static final int INTERNAL_ERROR = 7999;
    }

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Web3jException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return "Exception[" + getErrorCode() + "]: " + super.getMessage();
    }
}
