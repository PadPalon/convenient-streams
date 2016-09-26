package ch.neukom.convenientstreams;

import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * TODO document
 */
public class Try<R, E extends Exception> {
    public static <P, R, E extends Exception> Function<P, Try<R, E>> run(FunctionWithException<P, R> method, Class<E> exceptionClass) {
        return parameter -> {
            try {
                return new Try<>(method.apply(parameter));
            } catch (RuntimeException runtimeException) {
                throw runtimeException;
            } catch (Exception exception) {
                if(exceptionClass.isInstance(exception)) {
                    return new Try<>((E) exception);
                } else {
                    throw new IllegalStateException(String.format("Caught exception of type '%s' instead of '%s'", exception.getClass().getName(), exceptionClass.getName()));
                }
            }
        };
    }

    private final R value;
    private final E exception;

    private Try(R value) {
        this.value = value;
        this.exception = null;
    }

    private Try(E exception) {
        this.value = null;
        this.exception = exception;
    }

    public boolean success() {
        return exception == null;
    }

    public boolean failure() {
        return exception != null;
    }

    public R getValue() {
        Preconditions.checkNotNull(value, "Value can not be loaded when an exception occurred previously");
        return value;
    }

    public E getException() {
        Preconditions.checkNotNull(exception, "Exception can only be loaded when an exception occurred previously");
        return exception;
    }
}
