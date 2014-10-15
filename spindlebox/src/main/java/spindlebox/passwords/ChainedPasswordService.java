package spindlebox.passwords;

import com.google.inject.Inject;
import spindlebox.settings.AccountSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

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
            PasswordSupplier supplier = source.getPasswordSupplier(account);
            while (supplier.hasNext()) {
                String pw = supplier.next();
                if (attempt.apply(pw)) {
                    supplier.success(pw);
                    return true;
                }
            }
        }
        return false;
    }
}
