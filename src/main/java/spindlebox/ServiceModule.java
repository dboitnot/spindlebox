package spindlebox;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import spindlebox.AccountManager.AccountManagerFactory;
import spindlebox.MonitorSession.MonitorSessionFactory;
import spindlebox.passwords.ChainedPasswordService;
import spindlebox.passwords.PasswordService;
import spindlebox.settings.AccountSettings;
import spindlebox.settings.KvsSettingsSource;
import spindlebox.settings.Settings;
import spindlebox.settings.SettingsSource;
import spindlebox.util.KeyValueStore;
import spindlebox.util.PropertiesKeyValueStore;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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

        bind(SettingsSource.class).to(KvsSettingsSource.class);
        bind(PasswordService.class).to(ChainedPasswordService.class);
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

    @Provides
    ChainedPasswordService provideChainedPasswordService() {
        ChainedPasswordService ret = new ChainedPasswordService();
        ret.registerProvider(AccountSettings::getStoredPassword);
        return ret;
    }
}
