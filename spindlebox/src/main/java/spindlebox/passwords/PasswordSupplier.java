package spindlebox.passwords;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public interface PasswordSupplier extends Iterator<String> {
    default void success(String password) {}

    static PasswordSupplier of(Iterator<String> passwords) {
        return new PasswordSupplier() {
            @Override
            public boolean hasNext() {
                return passwords.hasNext();
            }

            @Override
            public String next() {
                return passwords.next();
            }
        };
    }

    static PasswordSupplier of(Collection<String> passwords) {
        return of(passwords.iterator());
    }

    static PasswordSupplier of(String... passwords) {
        return of(Arrays.asList(passwords));
    }
}
