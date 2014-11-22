package spindlebox.settings;

import spindlebox.util.KeyValueStore;

import java.time.Duration;
import java.util.Optional;

/**
 * spindlebox: KvsAccountSettings
 * Created by dboitnot on 10/8/14.
 */
public class KvsAccountSettings implements AccountSettings {
    private final KeyValueStore store;

    public KvsAccountSettings(KeyValueStore store) {
        this.store = store;
    }

    @Override
    public Optional<String> getTitle() {
        return store.get("title");
    }

    @Override
    public Optional<String> getHost() {
        return store.get("host");
    }

    @Override
    public Optional<String> getUsername() {
        return store.get("username");
    }

    @Override
    public Optional<String> getStoredPassword() {
        return store.get("password");
    }

    @Override
    public Optional<String> getInbox() {
        return store.get("inbox");
    }

    @Override
    public Optional<Duration> getInboxPollingInterval() {
        return store.getDuration("inboxPollingInterval");
    }

    @Override
    public Optional<Duration> getBoxHandlerPollingInterval() {
        return store.getDuration("boxHandlerPollingInterval");
    }

    @Override
    public Optional<Boolean> isDebug() {
        return store.getBoolean("debug");
    }

    @Override
    public Optional<Duration> getTimeout() {
        return store.getDuration("timeout");
    }
}
