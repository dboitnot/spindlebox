package spindlebox.handler.box;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * spindlebox: TomorrowBinHandler
 * Created by dboitnot on 10/11/14.
 */
public class TomorrowBinHandler extends DeferralBinHandler {
    @Override
    public String getFolderName() {
        return "Tomorrow";
    }

    @Override
    public LocalDateTime deferUntil(LocalDateTime from) {
        return from
                .with(HOUR_OF_DAY, 7)
                .with(MINUTE_OF_HOUR, 0)
                .plus(1, DAYS);
    }
}
