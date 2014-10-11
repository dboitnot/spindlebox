package spindlebox.util;

import java.util.Optional;

public interface KeyValueStore {
    public Optional<String> get(String key);

    public default Optional<Long> getLong(String key) {
        return get(key).map(Long::valueOf);
    }

    public default KeyValueStore child(String key) {
        return new PrefixedKeyValueStore(this, key + ".");
    }
}
