package spindlebox.handler.box;

import spindlebox.util.MailUtils;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static spindlebox.util.Logging.*;

/**
 * spindlebox: DeferralBinHandler
 * Created by dboitnot on 10/11/14.
 */
public abstract class DeferralBinHandler implements BoxHandler {
    public abstract String getFolderName();
    public abstract Instant deferUntil();

    public String getSigill() {
        return "@";
    }

    @Override
    public void process(Store store, Folder inbox) throws MessagingException {
        String folderName = getSigill() + getFolderName();
        Folder src = store.getFolder(folderName);
        if (!src.exists()) {
            DEBUG("Deferral bin doesn't exist: {}", folderName);
            return;
        }

        // Truncate timestamp to the minute
        String ts = String.valueOf(deferUntil()
                .truncatedTo(ChronoUnit.MINUTES)
                .toEpochMilli());
        Folder dest = DeferralHandler.getDeferredFolder(store).getFolder(ts);
        src.open(Folder.READ_WRITE);
        try {
            while (src.getMessageCount() > 0) {
                Message msg = src.getMessage(1);
                DEBUG("Deferring message to {}: {}", ts, msg.getSubject());
                MailUtils.move(msg, dest, true);
            }
        } finally {
            src.close(true);
        }
    }
}
