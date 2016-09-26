package ch.neukom.convenientstreams.example;

import ch.neukom.convenientstreams.Try;
import ch.neukom.convenientstreams.TryCollectors;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO document
 */
public class Test {
    public static void main(String[] params) {
        List<String> strings = ImmutableList.of("test", "second", "last");
        Boolean success = strings.stream()
                .map(Try.run(Test::writeData, PersistException.class))
                .collect(TryCollectors.isSuccess());
        System.out.println(success);

    }

    public static String writeData(String thing) throws PersistException {
        if(false) {
            throw new PersistException(thing);
        } else {
            return thing;
        }
    }

    public static class PersistException extends Exception {
        public PersistException(String message) {
            super(message);
        }
    }
}
