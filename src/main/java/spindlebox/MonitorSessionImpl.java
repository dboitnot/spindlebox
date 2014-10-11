package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.mail.imap.IMAPFolder;
import spindlebox.passwords.PasswordService;
import spindlebox.settings.AccountSettings;
import spindlebox.util.Logging;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import static spindlebox.util.Cleanup.with;

/**
 * spindlebox: MonitorSessionImpl
 * Created by dboitnot on 10/10/14.
 */
public class MonitorSessionImpl implements MonitorSession, Logging {
    private final AccountSettings settings;
    private final PasswordService passwordService;

    private Optional<Boolean> supportsIdle = Optional.empty();

    @Inject
    public MonitorSessionImpl(@Assisted AccountSettings settings, PasswordService passwordService) {
        this.settings = settings;
        this.passwordService = passwordService;
    }

    @Override
    public String getLoggingPrefix() {
        return settings.getLabel();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void start() throws FatalSessionException {
        try {
            DEBUG("Starting session");

            Properties prop = new Properties();
            prop.setProperty("mail.imap.starttls.enable", "true");

            // Get a Session object
            Session session = Session.getDefaultInstance(prop, null);
            session.setDebug(true);

            // Open the store
            with (session.getStore("imap"), store-> {
                String host = settings.getHost().orElseThrow(() -> new FatalSessionException("host not configured"));
                String user = settings.getUsername().orElseThrow(() -> new FatalSessionException("username not configured"));

                passwordService.applyPassword(settings, pw -> {
                    try {
                        DEBUG("Attempting to connect to {} as {}", host, user);
                        store.connect(host, user, pw);
                        return true;
                    } catch (MessagingException ex) {
                        return false;
                    }
                });
                DEBUG("Connected");

                // Open the inbox
                String inboxName = settings.getInbox().orElse("INBOX");
                Folder inbox = store.getFolder(inboxName);
                if (inbox == null || !inbox.exists())
                    throw new FatalSessionException("inbox folder not found: " + inboxName);
                inbox.open(Folder.READ_WRITE);
                addMessageHandler(inbox, this::handleInboxMessage);

                while (true) {
                    idle(inbox);
                }
            });
        } catch (NoSuchProviderException e) {
            throw new FatalSessionException(e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected exception for session: " + settings.getLabel(), e);
        }
    }

    private void realIdle(Folder folder) throws MessagingException {
        if (folder instanceof IMAPFolder) {
            IMAPFolder f = (IMAPFolder)folder;
            f.idle();
        }
    }

    private void fakeIdle(Folder folder) throws MessagingException {
        try {
            Thread.sleep(settings.getPollingInterval().orElse(5000L));
            folder.getMessageCount();
        } catch (InterruptedException ignored) {}
    }

    private void idle(Folder folder) throws MessagingException {
        if (supportsIdle.isPresent()) {
            // We've already tried to idle, so follow the previous result.
            if (supportsIdle.get()) realIdle(folder); else fakeIdle(folder);
        } else {
            // Try once to idle.
            try {
                realIdle(folder);
                supportsIdle = Optional.of(true);
            } catch (MessagingException ex) {
                INFO("IDLE not supported: {}", ex.getMessage());
                supportsIdle = Optional.of(false);
                fakeIdle(folder);
            }
        }
    }

    private void addMessageHandler(Folder folder, Consumer<Message> c) {
        folder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                Message[] messages = e.getMessages();
                DEBUG("Got {} new messages in folder {}", messages.length, folder.getName());
                for (Message msg : messages)
                    c.accept(msg);
            }
        });
    }

    private void handleInboxMessage(Message msg) {
        try {
            DEBUG("handleInboxMessage: {}", msg.getSubject());
        } catch (MessagingException e) {
            WARN("Error while handling inbox message.");
        }
    }
}
