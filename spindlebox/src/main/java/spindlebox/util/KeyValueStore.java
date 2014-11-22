package spindlebox.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static spindlebox.util.Logging.WARN;

public interface KeyValueStore {
    public Optional<String> get(String key);

    public default Optional<Long> getLong(String key) {
        return get(key).map(Long::valueOf);
    }

    public default Optional<Boolean> getBoolean(String key) {
        return get(key).map(Boolean::valueOf);
    }

    public default Optional<Duration> getDuration(String key) {
        return get(key).flatMap(s -> {
            if (s.length() < 1)
                return Optional.empty();

            // If string is a number, interpret it as milliseconds
            if (StringUtils.isNumeric(s))
                return Optional.of(Duration.ofMillis(Long.valueOf(s)));

            // Otherwise, it should be in the format <number>:<unit>
            String[] parts = s.split(":");
            if (parts.length != 2) {
                WARN("Malformed duration string: " + s);
                return Optional.empty();
            }

            if (!StringUtils.isNumeric(parts[0])) {
                WARN("Malformed duration string: " + s);
                return Optional.empty();
            }

            try {
                return Optional.of(Duration.of(Long.valueOf(parts[0]), ChronoUnit.valueOf(parts[1])));
            } catch (IllegalArgumentException e) {
                WARN("Malformed duration string: " + s);
                return Optional.empty();
            }
        });
    }

    public default KeyValueStore child(String key) {
        return new PrefixedKeyValueStore(this, key + ".");
    }
}
