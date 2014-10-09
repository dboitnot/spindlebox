package spindlebox.util;

import java.util.Optional;

public interface KeyValueStore {
    public Optional<String> get(String key);

    public default KeyValueStore child(String key) {
        return new PrefixedKeyValueStore(this, key + ".");
    }
}
