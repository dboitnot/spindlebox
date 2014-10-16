package spindlebox.passwords;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import spindlebox.settings.AccountSettings;

import java.util.Optional;
import java.util.function.Supplier;

import static spindlebox.util.Logging.*;

public interface PasswordSource {
    public Supplier<Optional<String>> getPasswordSupplier(AccountSettings settings);

    static <T extends PasswordSource> Module moduleFor(Class<T> cls) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                DEBUG("Installing {}", cls);
                Multibinder<PasswordSource> binder = Multibinder.newSetBinder(binder(), PasswordSource.class);
                binder.addBinding().to(cls);
            }
        };
    }
}
