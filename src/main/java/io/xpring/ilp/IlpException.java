package io.xpring.ilp;

public class IlpException extends Exception {
    /** The type of exception. */
    private IlpExceptionType type;

    /**
     * Create a new exception.
     *
     * @param message The message to to include in the exception
     */
    public IlpException(IlpExceptionType type, String message) {
        super(message);

        this.type = type;
    }

    /**
     * @return The exception type.
     */
    public IlpExceptionType getType() {
        return this.type;
    }
}