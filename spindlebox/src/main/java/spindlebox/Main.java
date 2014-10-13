package spindlebox;

import com.google.inject.Guice;
import com.google.inject.Injector;
import spindlebox.AccountManager.AccountManagerFactory;
import spindlebox.settings.SettingsSource;
import static spindlebox.util.Logging.*;

/**
 * spindlebox: Main
 * Created by dboitnot on 10/7/14.
 */
public class Main {
    public static void main(String[] args) {
        INFO("Spindlebox starting up...");
        Injector injector = Guice.createInjector(new ServiceModule());

        SettingsSource settingsSource = injector.getInstance(SettingsSource.class);

        settingsSource.getAccounts().forEach(accountSettings -> injector
                .getInstance(AccountManagerFactory.class)
                .create(accountSettings)
                .start());
    }
}
