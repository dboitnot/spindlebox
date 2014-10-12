package spindlebox.handler.box;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import spindlebox.util.Logging;
import spindlebox.util.MailUtils;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * spindlebox: DeferralHandler
 * Created by dboitnot on 10/11/14.
 */
public class DeferralHandler implements BoxHandler, Logging {
    public static class Module extends AbstractModule {
        @Override
        protected void configure() {
            Multibinder<BoxHandler> binder = Multibinder.newSetBinder(binder(), BoxHandler.class);
            binder.addBinding().to(DeferralHandler.class);
        }
    }

    @Override
    public void process(Store store, Folder inbox) throws MessagingException {
        Folder deferredFolder = getDeferredFolder(store);
        if (deferredFolder.exists()) {
            for (Folder df : deferredFolder.list())
                handleDeferredFolder(df, inbox);
        } else {
            DEBUG("Deferred folder does not exist");
        }
    }

    private void handleDeferredFolder(Folder df, Folder inbox) throws MessagingException {
        long ts;
        try {
            ts = Long.valueOf(df.getName());
        } catch (Exception ex) {
            WARN("Unable to parse deferral folder name '{}'", df.getName());
            return;
        }

        if (ts <= System.currentTimeMillis()) {
            try {
                // Process the messages one at a time in case another process is also working in this folder
                df.open(Folder.READ_WRITE);
                while (df.getMessageCount() > 0) {
                    Message msg = df.getMessage(1);
                    DEBUG("Re-inbox-ing deferred message '{}' from folder {}", msg.getSubject(), ts);
                    msg.setFlag(Flag.SEEN, false);
                    MailUtils.move(msg, inbox, false);
                }
                df.close(true);

                DEBUG("Deleting empty deferral folder {}", ts);
                df.delete(false);
            } finally {
                if (df.isOpen())
                    df.close(false);
            }
        } else {
            DEBUG("Not yet time for deferral folder {}", ts);
        }
    }

    public static Folder getDeferredFolder(Store store) throws MessagingException {
        return store.getFolder("Deferred");
    }
}
