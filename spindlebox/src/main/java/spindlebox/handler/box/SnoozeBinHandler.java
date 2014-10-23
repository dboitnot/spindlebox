package spindlebox.handler.box;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.HOURS;

/**
 * spindlebox: SnoozeBinHandler
 * Created by dboitnot on 10/23/14.
 */
public class SnoozeBinHandler extends DeferralBinHandler {
    @Override
    public String getFolderName() {
        return "Snooze";
    }

    @Override
    public LocalDateTime deferUntil(LocalDateTime from) {
        return from.plus(3, HOURS);
    }
}
