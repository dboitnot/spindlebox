package spindlebox.handler.box;

import java.time.LocalDateTime;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoUnit.DAYS;

public class NextWeekBinHandler extends DeferralBinHandler {
    @Override
    public String getFolderName() {
        return "NextWeek";
    }

    @Override
    public LocalDateTime deferUntil(LocalDateTime from) {
        return from
                .with(HOUR_OF_DAY, 7)
                .with(MINUTE_OF_HOUR, 0)
                .with(DAY_OF_WEEK, MONDAY.getValue())
                .plus(7, DAYS);
    }
}
