package spindlebox.passwords;

import com.google.inject.Inject;
import spindlebox.settings.AccountSettings;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static spindlebox.util.Logging.DEBUG;

/**
 * spindlebox: ChainedPasswordService
 * Created by dboitnot on 10/9/14.
 */
public class ChainedPasswordService implements PasswordService {
    private final Set<PasswordSource> passwordSources;

    @Inject
    public ChainedPasswordService(Set<PasswordSource> passwordSources) {
        this.passwordSources = passwordSources;
    }

    @Override
    public boolean applyPassword(AccountSettings account, Function<String, Boolean> attempt) {
        for (PasswordSource source : passwordSources) {
            Supplier<Optional<String>> supplier = source.getPasswordSupplier(account);
            for (Optional<String> pw = supplier.get(); pw.isPresent(); pw = supplier.get()) {
                DEBUG("Trying a password from {}", source.getClass());
                if (attempt.apply(pw.get()))
                    return true;
            }
        }
        return false;
    }
}
