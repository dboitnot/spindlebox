package spindlebox.util;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * spindlebox: Cleanup
 * Created by dboitnot on 10/11/14.
 */
public class Cleanup {
    public static interface ThrowingConsumer<T,E extends Throwable> {
        public void accept(T value) throws E;
    }

    public static <T,E extends Throwable> void with(T resource, ThrowingConsumer<T,E> block, Consumer<T> cleanup)
            throws E
    {
        try {
            block.accept(resource);
        } finally {
            cleanup.accept(resource);
        }
    }

    public static <T,E extends Throwable> void with(T resource, ThrowingConsumer<T,E> block) throws E {
        if (resource == null)
            throw new RuntimeException("resource cannot be null in a 'with' statement");

        Consumer<T> cleanup;
        if (resource instanceof AutoCloseable) {
            cleanup = r -> {
                try {
                    ((AutoCloseable) r).close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        } else {
            try {
                Method method = resource.getClass().getMethod("close");
                cleanup = r -> {
                    try {
                        method.invoke(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("No close method found for resource in 'with' statement");
            }
        }

        with(resource, block, cleanup);
    }
}
