package spindlebox.handler.box;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

import java.time.Instant;
import java.time.OffsetDateTime;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * spindlebox: TomorrowBinHandler
 * Created by dboitnot on 10/11/14.
 */
public class TomorrowBinHandler extends DeferralBinHandler {
    public static class Module extends AbstractModule {
        @Override
        protected void configure() {
            Multibinder<BoxHandler> binder = Multibinder.newSetBinder(binder(), BoxHandler.class);
            binder.addBinding().to(TomorrowBinHandler.class);
        }
    }

    @Override
    public String getFolderName() {
        return "Tomorrow";
    }

    @Override
    public Instant deferUntil() {
        return OffsetDateTime.now()
                .with(HOUR_OF_DAY, 7)
                .with(MINUTE_OF_HOUR, 0)
                .plus(1, DAYS)
                .toInstant();
    }
}
