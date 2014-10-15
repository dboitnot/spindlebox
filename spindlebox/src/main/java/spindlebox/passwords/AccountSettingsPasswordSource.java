package spindlebox.passwords;

import spindlebox.settings.AccountSettings;

import java.util.Optional;

public class AccountSettingsPasswordSource implements PasswordSource {
    @Override
    public PasswordSupplier getPasswordSupplier(AccountSettings settings) {
        Optional<String> o = settings.getStoredPassword();
        return o.isPresent() ? PasswordSupplier.of(o.get()) : PasswordSupplier.of();
    }
}
