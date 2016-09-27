package ch.neukom.convenientstreams;

import java.util.function.Function;

/**
 * represents a try-catch as an object
 * contains the value of an executed function or the exception it has thrown
 *
 * @param <R> the type of the value
 * @param <E> the type of the exception
 */
public class Try<R, E extends Exception> {
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

    /**
     * wraps a given function into one that returns a {@link Try} and catches exceptions
     * a thrown Exception of type {@link E} gets caught and added to the Try
     * any {@link RuntimeException} are rethrown
     * any other thrown Exception is wrapped in a {@link IllegalStateException}
     *
     * @param function the function to wrap
     * @param exceptionClass the exception to catch
     * @param <P> parameter type of the function
     * @param <R> return type of the function
     * @param <E> exception type to catch
     * @return the wrapped function
     */
    public static <P, R, E extends Exception> Function<P, Try<R, E>> runCatch(FunctionWithException<P, R> function, Class<E> exceptionClass) {
        return parameter -> {
            try {
                return new Try<>(function.apply(parameter));
            } catch (RuntimeException runtimeException) {
                throw runtimeException;
            } catch (Exception exception) {
                if(exceptionClass.isInstance(exception)) {
                    return new Try<>((E) exception);
                } else {
                    throw new IllegalStateException(String.format("Caught exception of type '%s' instead of '%s'", exception.getClass().getName(), exceptionClass.getName()), exception);
                }
            }
        };
    }

    /**
     * wraps a given function into one that returns a {@link Try} and catches exceptions
     * all thrown exceptions are caught and added to the Try
     *
     * @param function the function to wrap
     * @param <P> parameter type of the function
     * @param <R> return type of the function
     * @return the wrapped function
     */
    public static <P, R> Function<P, Try<R, Exception>> runCatchAll(FunctionWithException<P, R> function) {
        return parameter -> {
            try {
                return new Try<>(function.apply(parameter));
            } catch (Exception exception) {
                return new Try<>(exception);
            }
        };
    }

    /**
     * @return true if no exception was thrown, false otherwise
     */
    public boolean success() {
        return exception == null;
    }

    /**
     *
     * @return true if exception was thrown, false otherwise
     */
    public boolean failure() {
        return exception != null;
    }

    /**
     * @return the value of the executed function
     * @throws IllegalStateException thrown if exception was thrown
     */
    public R getValue() {
        if(value == null) {
            throw new IllegalStateException("Value can not be loaded when an exception occurred previously");
        }
        return value;
    }

    /**
     * @return the exception thrown by the excuted function
     * @throws IllegalStateException thrown if no exception was thrown
     */
    public E getException() {
        if(exception == null) {
            throw new IllegalStateException("Exception can only be loaded when an exception occurred previously");
        }
        return exception;
    }
}
