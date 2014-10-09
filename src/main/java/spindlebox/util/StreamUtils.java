package spindlebox.util;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * spindlebox: StreamUtils
 * Created by dboitnot on 10/8/14.
 */
public class StreamUtils {
    public static <T> Stream<T> toStream(T[] array, boolean parallel) {
        return StreamSupport.stream(Arrays.spliterator(array), parallel);
    }

    public static <T> Stream<T> toParallelStream(T[] array) {
        return toStream(array, true);
    }

    public static <T> Stream<T> toSerialStream(T[] array) {
        return toStream(array, false);
    }
}
