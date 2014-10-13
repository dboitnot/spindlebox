package spindlebox.util;

import java.util.Optional;
import java.util.Properties;

/**
 * spindlebox: PropertiesKeyValueStore
 * Created by dboitnot on 10/8/14.
 */
public class PropertiesKeyValueStore implements KeyValueStore {
    private final Properties p;

    public PropertiesKeyValueStore(Properties p) {
        this.p = p;
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(p.getProperty(key));
    }
}
