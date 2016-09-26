package ch.neukom.convenientstreams;

/**
 * TODO document
 */
@FunctionalInterface
public interface FunctionWithException<P, R> {
    R apply(P parameter) throws Exception;
}
