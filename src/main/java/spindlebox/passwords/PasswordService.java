package spindlebox.passwords;

import spindlebox.settings.AccountSettings;

import java.util.function.Function;

/**
 * spindlebox: PasswordService
 * Created by dboitnot on 10/9/14.
 */
public interface PasswordService {
    public boolean applyPassword(AccountSettings account, Function<String,Boolean> attempt);
}
