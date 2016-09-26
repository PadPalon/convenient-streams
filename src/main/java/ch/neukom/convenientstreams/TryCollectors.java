package ch.neukom.convenientstreams;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * TODO document
 */
public class TryCollectors {
    public static <P, E extends Exception> Collector<Try<P, E>, HashSet<Exception>, Boolean> isSuccess() {
        return new Collector<Try<P, E>, HashSet<Exception>, Boolean>() {
            @Override
            public Supplier<HashSet<Exception>> supplier() {
                return HashSet::new;
            }

            @Override
            public BiConsumer<HashSet<Exception>, Try<P, E>> accumulator() {
                return (exceptions, trying) -> {
                    if(trying.failure()) {
                        exceptions.add(trying.getException());
                    }
                };
            }

            @Override
            public BinaryOperator<HashSet<Exception>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<HashSet<Exception>, Boolean> finisher() {
                return HashSet::isEmpty;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectSuccess() {
        return new Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>>() {
            @Override
            public Supplier<ArrayList<Try<P, E>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<Try<P, E>>, Try<P, E>> accumulator() {
                return (list, trying) -> {
                    if(trying.success()) {
                        list.add(trying);
                    }
                };
            }

            @Override
            public BinaryOperator<ArrayList<Try<P, E>>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<ArrayList<Try<P, E>>, Stream<P>> finisher() {
                return list -> list.stream().map(Try::getValue);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectAndThrow() {
        return new Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>>() {
            @Override
            public Supplier<ArrayList<Try<P, E>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<Try<P, E>>, Try<P, E>> accumulator() {
                return (list, trying) -> {
                    if(trying.success()) {
                        list.add(trying);
                    } else {
                        throw new TryCollectorException(trying.getException());
                    }
                };
            }

            @Override
            public BinaryOperator<ArrayList<Try<P, E>>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<ArrayList<Try<P, E>>, Stream<P>> finisher() {
                return list -> list.stream().map(Try::getValue);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    public static <P, E extends Exception> Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>> collectUntilException() {
        return new Collector<Try<P, E>, ArrayList<Try<P, E>>, Stream<P>>() {
            private boolean failureFound = false;

            @Override
            public Supplier<ArrayList<Try<P, E>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<ArrayList<Try<P, E>>, Try<P, E>> accumulator() {
                return (list, trying) -> {
                    if(!failureFound && trying.success()) {
                        list.add(trying);
                    } else {
                        failureFound = true;
                    }
                };
            }

            @Override
            public BinaryOperator<ArrayList<Try<P, E>>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<ArrayList<Try<P, E>>, Stream<P>> finisher() {
                return list -> list.stream().map(Try::getValue);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    private static class TryCollectorException extends RuntimeException {
        public TryCollectorException(Throwable cause) {
            super(cause);
        }
    }
}
