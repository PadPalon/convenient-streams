package ch.neukom.convenientstreams;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * collection of collectors to be used on {@link Try}
 */
public class TryCollectors {
    private TryCollectors() {
    }

    /**
     * @param <P> value type of Try
     * @param <E> exception type of Try
     * @return a collector that returns true if no exception was caught in any Try, false otherwise
     */
    public static <P, E extends Exception> Collector<Try<P, E>, HashSet<E>, Boolean> isSuccess() {
        return hasState(true);
    }

    /**
     * @param <P> value type of Try
     * @param <E> exception type of Try
     * @return a collector that returns true if an exception was caught in any Try, false otherwise
     */
    public static <P, E extends Exception> Collector<Try<P, E>, HashSet<E>, Boolean> isFailure() {
        return hasState(false);
    }

    private static <P, E extends Exception> Collector<Try<P, E>, HashSet<E>, Boolean> hasState(boolean isSuccess) {
        return new Collector<Try<P, E>, HashSet<E>, Boolean>() {
            @Override
            public Supplier<HashSet<E>> supplier() {
                return HashSet::new;
            }

            @Override
            public BiConsumer<HashSet<E>, Try<P, E>> accumulator() {
                return (exceptions, trying) -> {
                    if(isSuccess ? trying.success() : trying.failure()) {
                        exceptions.add(trying.getException());
                    }
                };
            }

            @Override
            public BinaryOperator<HashSet<E>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<HashSet<E>, Boolean> finisher() {
                return HashSet::isEmpty;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    /**
     * @param <P> value type of Try
     * @param <E> exception type of Try
     * @return a collector that collects the values of all successfully executed functions
     */
    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectSuccess() {
        return collectList((list, trying) -> {
            if(trying.success()) {
                list.add(trying);
            }
        });
    }

    /**
     * @param <P> value type of Try
     * @param <E> exception type of Try
     * @return a collector that assumes all functions where executed successfully and collects their values, throws a {@link TryCollectorException} otherwise
     */
    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectOrThrow() {
        return collectList((list, trying) -> {
            if(trying.success()) {
                list.add(trying);
            } else {
                throw new TryCollectorException(trying.getException());
            }
        });
    }

    /**
     * @param <P> value type of Try
     * @param <E> exception type of Try
     * @return a collector that collects the values of all executed functions up until the first caught exception
     */
    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectUntilException() {
        List<Exception> exceptions = Lists.newArrayList();
        return collectList((list, trying) -> {
            if(exceptions.isEmpty() && trying.success()) {
                list.add(trying);
            } else {
                exceptions.add(trying.getException());
            }
        });
    }

    /**
     * @param <P> value type of Try
     * @param <E> exception type of Try
     * @return a collector that collects the values of all executed functions up until the first caught exception
     */
    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<E>> collectExceptions() {
        return collectList((list, trying) -> {
            if(trying.failure()) {
                list.add(trying);
            }
        }, Try::getException);
    }

    private static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectList(BiConsumer<ArrayList<Try<P, E>>, Try<P, E>> accumulator) {
        return collectList(accumulator, Try::getValue);
    }

    private static <P, E extends Exception, R> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<R>> collectList(BiConsumer<ArrayList<Try<P, E>>, Try<P, E>> accumulator, Function<Try<P, E>, R> finisher) {
        return new Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<R>>() {
            @Override
            public Supplier<ArrayList<Try<P, E>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<Try<P, E>>, Try<P, E>> accumulator() {
                return accumulator;
            }

            @Override
            public BinaryOperator<ArrayList<Try<P, E>>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<ArrayList<Try<P, E>>, Stream<R>> finisher() {
                return list -> list.stream().map(finisher);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    /**
     *
     */
    private static class TryCollectorException extends RuntimeException {
        public TryCollectorException(Throwable cause) {
            super(cause);
        }
    }
}
