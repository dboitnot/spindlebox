package spindlebox.passwords;

import spindlebox.settings.AccountSettings;
import spindlebox.util.Supply;

import java.util.Optional;
import java.util.function.Supplier;

public class AccountSettingsPasswordSource implements PasswordSource {
    @Override
    public Supplier<Optional<String>> getPasswordSupplier(AccountSettings settings) {
        return Supply.once(settings.getStoredPassword());
    }
}
