package spindlebox.passwords;

import net.east301.keyring.Keyring;
import spindlebox.settings.AccountSettings;
import spindlebox.util.Supply;

import java.util.Optional;
import java.util.function.Supplier;

import static spindlebox.util.Logging.*;

public class KeyringPasswordSource implements PasswordSource {
    @Override
    public Supplier<Optional<String>> getPasswordSupplier(AccountSettings settings) {
        return Supply.once(() -> {
            try {
                DEBUG("Opening keyring");
                Keyring keyring = Keyring.create();
                if (!settings.getHost().isPresent())
                    return Optional.empty();
                if (!settings.getUsername().isPresent())
                    return Optional.empty();
                DEBUG("Fetching password for {}@{}", settings.getUsername().get(), settings.getHost().get());
                return Optional.ofNullable(keyring.getPassword(settings.getHost().get(), settings.getUsername().get()));
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }
}
