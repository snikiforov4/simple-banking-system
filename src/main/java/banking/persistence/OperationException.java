package banking.persistence;

public class OperationException extends RuntimeException {

    private final Code errorCode;

    public OperationException(Code errorCode) {
        this.errorCode = errorCode;
    }

    public OperationException(Code errorCode, Throwable throwable) {
        super(throwable);
        this.errorCode = errorCode;
    }

    enum Code {
        ENTITY_NOT_FOUND
    }
}
