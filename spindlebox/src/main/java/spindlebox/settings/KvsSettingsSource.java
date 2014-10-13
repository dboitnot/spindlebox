package spindlebox.settings;

import com.google.inject.Inject;
import spindlebox.util.KeyValueStore;

import java.util.List;
import java.util.stream.Collectors;

import static spindlebox.util.StreamUtils.*;

/**
 * spindlebox: SettingsSourceImpl
 * Created by dboitnot on 10/8/14.
 */
public class KvsSettingsSource implements SettingsSource {
    private final KeyValueStore store;

    @Inject
    public KvsSettingsSource(@Settings KeyValueStore store) {
        this.store = store;
    }

    @Override
    public List<AccountSettings> getAccounts() {
        return toSerialStream(store.get("accounts").orElse("").split(","))
                .map(key -> new KvsAccountSettings(store.child(key)))
                .collect(Collectors.toList());
    }
}
