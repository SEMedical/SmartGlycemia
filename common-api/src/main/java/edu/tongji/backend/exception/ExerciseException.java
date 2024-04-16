package edu.tongji.backend.exception;

public class ExerciseException extends RuntimeException{
    public ExerciseException() {
        super();
    }

    public ExerciseException(String message) {
        super("Expected exercise exception under glycemia module:"+message);
    }

    public ExerciseException(String message, Throwable cause) {
        super("Expected chained exercise exception:"+message, cause);
    }

    public ExerciseException(Throwable cause) {
        super("Expected chained exercise exception:"+cause);
    }
}
