package spindlebox.logging;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

/**
 * spindlebox: LoggingModule
 * Created by dboitnot on 10/10/14.
 */
public class LoggingModule extends AbstractModule {
    @Override
    protected void configure() {
        bindListener(Matchers.any(), new Listener());
    }

    private static class Listener implements TypeListener {
        @Override
        public <I> void hear(TypeLiteral<I> literal, TypeEncounter<I> encounter) {
            for (Field field : literal.getRawType().getDeclaredFields()) {
                if (field.getType() == Logger.class) {
                    encounter.register(new Injector<>(field));
                }
            }
        }
    }

    private static class Injector<T> implements MembersInjector<T> {
        private final Field field;
        private final Logger logger;

        private Injector(Field field) {
            this.field = field;
            logger = LogManager.getLogger(field.getDeclaringClass());
            field.setAccessible(true);
        }

        @Override
        public void injectMembers(T t) {
            try {
                field.set(t, logger);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
