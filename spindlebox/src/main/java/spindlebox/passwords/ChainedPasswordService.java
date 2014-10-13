package spindlebox.passwords;

import spindlebox.settings.AccountSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * spindlebox: ChainedPasswordService
 * Created by dboitnot on 10/9/14.
 */
public class ChainedPasswordService implements PasswordService {
    private final List<Function<AccountSettings,Optional<String>>> providers = new ArrayList<>();

    public void registerProvider(Function<AccountSettings,Optional<String>> p) {
        providers.add(p);
    }

    @Override
    public boolean applyPassword(AccountSettings account, Function<String, Boolean> attempt) {
        for (Function<AccountSettings, Optional<String>> provider : providers) {
            Optional<String> candidate = provider.apply(account);
            if (!candidate.isPresent())
                continue;
            if (attempt.apply(candidate.get()))
                return true;
        }
        return false;
    }
}
