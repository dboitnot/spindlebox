package spindlebox.settings;

import spindlebox.util.KeyValueStore;

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
    public Optional<Long> getInboxPollingInterval() {
        return store.getLong("inboxPollingInterval");
    }

    @Override
    public Optional<Long> getBoxHandlerPollingInterval() {
        return store.getLong("boxHandlerPollingInterval");
    }

    @Override
    public Optional<Boolean> isDebug() {
        return store.getBoolean("debug");
    }
}
