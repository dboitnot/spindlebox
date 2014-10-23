package spindlebox.handler.box;

import spindlebox.util.MailUtils;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static spindlebox.util.Logging.DEBUG;

/**
 * spindlebox: DeferralBinHandler
 * Created by dboitnot on 10/11/14.
 */
public abstract class DeferralBinHandler implements BoxHandler {
    public abstract String getFolderName();
    public abstract LocalDateTime deferUntil(LocalDateTime from);

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
        } else {
            DEBUG("Processing deferral bin: {}", folderName);
        }

        // Truncate timestamp to the minute
        String ts = deferUntil(LocalDateTime.now())
                .truncatedTo(ChronoUnit.MINUTES)
                .toString();
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
