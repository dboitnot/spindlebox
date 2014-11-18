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
        DEBUG("Initializing Guice");
        Injector injector = Guice.createInjector(new ServiceModule());
        injector.getInstance(AppControl.class).startup();
    }
}
