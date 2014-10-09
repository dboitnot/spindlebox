package spindlebox.settings;

import java.util.Optional;

/**
 * spindlebox: AccountSettings
 * Created by dboitnot on 10/7/14.
 */
public interface AccountSettings {
    public Optional<String> getTitle();
    public Optional<String> getHost();
    public Optional<String> getUsername();

    public default Optional<String> getStoredPassword() {
        return Optional.empty();
    }
}
