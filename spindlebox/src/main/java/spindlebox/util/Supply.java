package spindlebox.util;

import java.util.Optional;
import java.util.function.Supplier;

public class Supply {
    public static <T> Supplier<Optional<T>> once(Optional<T> v) {
        return new Supplier<Optional<T>>() {
            boolean tried = false;

            @Override
            public Optional<T> get() {
                if (tried)
                    return Optional.empty();
                tried = true;
                return v;
            }
        };
    }

    public static <T> Supplier<Optional<T>> once(Supplier<Optional<T>> s) {
        return new Supplier<Optional<T>>() {
            boolean tried = false;

            @Override
            public Optional<T> get() {
                if (tried)
                    return Optional.empty();
                tried = true;
                return s.get();
            }
        };
    }
}
