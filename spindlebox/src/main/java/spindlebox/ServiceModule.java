package spindlebox;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import spindlebox.AccountManager.AccountManagerFactory;
import spindlebox.MonitorSession.MonitorSessionFactory;
import spindlebox.handler.box.BoxHandler;
import spindlebox.handler.box.DeferralHandler;
import spindlebox.handler.box.NextWeekBinHandler;
import spindlebox.handler.box.TomorrowBinHandler;
import spindlebox.passwords.AccountSettingsPasswordSource;
import spindlebox.passwords.ChainedPasswordService;
import spindlebox.passwords.PasswordService;
import spindlebox.passwords.PasswordSource;
import spindlebox.settings.AccountSettings;
import spindlebox.settings.KvsSettingsSource;
import spindlebox.settings.Settings;
import spindlebox.settings.SettingsSource;
import spindlebox.util.KeyValueStore;
import spindlebox.util.PropertiesKeyValueStore;
import spindlebox.util.Shared;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * spindlebox: ServiceModule
 * Created by dboitnot on 10/7/14.
 */
public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(MonitorSession.class, MonitorSessionImpl.class)
                .build(MonitorSessionFactory.class));

        install(new FactoryModuleBuilder()
                .implement(AccountManager.class, AccountManagerImpl.class)
                .build(AccountManagerFactory.class));

        install(BoxHandler.moduleFor(DeferralHandler.class));
        install(BoxHandler.moduleFor(TomorrowBinHandler.class));
        install(BoxHandler.moduleFor(NextWeekBinHandler.class));

        install(PasswordSource.moduleFor(AccountSettingsPasswordSource.class));

        bind(SettingsSource.class).to(KvsSettingsSource.class);
        bind(PasswordService.class).to(ChainedPasswordService.class);

        bind(ScheduledExecutorService.class)
                .annotatedWith(Shared.class)
                .toInstance(Executors.newScheduledThreadPool(1));
    }

    @Provides @Settings
    KeyValueStore provideTestKvs() {
        try (InputStream in = new FileInputStream("spindlebox_test_settings.properties")) {
            Properties p = new Properties();
            p.load(in);
            return new PropertiesKeyValueStore(p);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
