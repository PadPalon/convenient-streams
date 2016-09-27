package ch.neukom.convenientstreams;

/**
 * basically the same as {@link java.util.function.Function} but throws any Exception
 *
 * @param <P> type of the parameter value
 * @param <R> type of the return value
 */
@FunctionalInterface
public interface FunctionWithException<P, R> {
    /**
     * runs function
     * @param parameter the parameter to the function
     * @return the return value
     * @throws Exception any Exception
     */
    R apply(P parameter) throws Exception;
}
