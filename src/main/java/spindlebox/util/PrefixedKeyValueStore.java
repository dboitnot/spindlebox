package spindlebox.util;

import java.util.Optional;

/**
 * spindlebox: PrefixedKeyValueStore
 * Created by dboitnot on 10/8/14.
 */
public class PrefixedKeyValueStore implements KeyValueStore {
    private final KeyValueStore delegate;
    private final String prefix;

    public PrefixedKeyValueStore(KeyValueStore delegate, String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }

    @Override
    public Optional<String> get(String key) {
        return delegate.get(prefix + key);
    }
}
