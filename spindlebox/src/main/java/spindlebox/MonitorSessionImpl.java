package spindlebox;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.mail.imap.IMAPFolder;
import spindlebox.handler.box.BoxHandler;
import spindlebox.passwords.PasswordService;
import spindlebox.settings.AccountSettings;
import spindlebox.ui.StateMonitor;
import spindlebox.util.Shared;

import javax.mail.*;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static spindlebox.ui.StateMonitor.State.*;
import static spindlebox.util.Cleanup.with;
import static spindlebox.util.Logging.*;

/**
 * spindlebox: MonitorSessionImpl
 * Created by dboitnot on 10/10/14.
 */
public class MonitorSessionImpl implements MonitorSession {
    private final AccountSettings settings;
    private final PasswordService passwordService;
    private final Set<BoxHandler> boxHandlers;
    private final ScheduledExecutorService sharedPool;
    private final StateMonitor stateMonitor;

    private Optional<Boolean> supportsIdle = Optional.empty();

    @Inject
    public MonitorSessionImpl(
            @Assisted AccountSettings settings,
            PasswordService passwordService,
            Set<BoxHandler> boxHandlers,
            @Shared ScheduledExecutorService sharedPool,
            StateMonitor stateMonitor)
    {
        this.settings = settings;
        this.passwordService = passwordService;
        this.boxHandlers = boxHandlers;
        this.sharedPool = sharedPool;
        this.stateMonitor = stateMonitor;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void start(AtomicReference<Optional<Runnable>> closeHandle) throws FatalSessionException {
        try {
            DEBUG("Starting session");
            stateMonitor.stateChanged(CONNECTING);

            Properties prop = new Properties();
            prop.setProperty("mail.imap.starttls.enable", "true");

            // Set timeouts
            String timeout = String.valueOf(settings.getTimeout().orElse(Duration.ofSeconds(45)).toMillis());
            prop.setProperty("mail.imap.connectiontimeout", timeout);
            prop.setProperty("mail.imap.timeout", timeout);

            // Get a Session object
            Session session = Session.getDefaultInstance(prop, null);
            session.setDebug(settings.isDebug().orElse(false));

            // Open the store
            with (session.getStore("imap"), store-> {
                String host = settings.getHost().orElseThrow(() -> new FatalSessionException("host not configured"));
                String user = settings.getUsername().orElseThrow(() -> new FatalSessionException("username not configured"));

                if (passwordService.applyPassword(settings, pw -> {
                    try {
                        DEBUG("Attempting to connect to {} as {}", host, user);
                        store.connect(host, user, pw);
                        return store.isConnected();
                    } catch (MessagingException ex) {
                        return false;
                    }
                })) {
                    DEBUG("Connected");
                } else {
                    return;
                    //throw new FatalSessionException("no more passwords to try, giving up");
                }

                // Set up the close handle
                final WeakReference<Store> storeWeakReference = new WeakReference<>(store);
                final AtomicBoolean shuttingDown = new AtomicBoolean(false);
                closeHandle.set(Optional.of(() -> {
                    shuttingDown.set(true);
                    Store s = storeWeakReference.get();
                    if (s == null)
                        return;
                    try {
                        s.close();
                    } catch (MessagingException e) {
                        INFO("Error closing store", e);
                    }
                }));

                // Open the inbox
                stateMonitor.stateChanged(PROCESSING);
                String inboxName = settings.getInbox().orElse("INBOX");
                Folder inbox = store.getFolder(inboxName);
                if (inbox == null || !inbox.exists())
                    throw new FatalSessionException("inbox folder not found: " + inboxName);
                inbox.open(Folder.READ_WRITE);
                addMessageHandler(inbox, this::handleInboxMessage);

                while (!shuttingDown.get()) {
                    stateMonitor.stateChanged(PROCESSING);
                    // Let the box handlers do their thing before idling.
                    boxHandlers.forEach(h -> {
                        try {
                            h.process(store, inbox);
                        } catch (MessagingException e) {
                            if (e.getCause() instanceof StoreClosedException) {
                                WARN("Store closed unexpectedly.");
                                return;
                            }
                            WARN("error in boxHandler {}", h.getClass(), e);
                        }
                    });

                    // Wait for new new messages to arrive in the inbox and handle them.
                    stateMonitor.stateChanged(WAITING);
                    idle(inbox);
                }
            });
        } catch (NoSuchProviderException e) {
            throw new FatalSessionException(e);
        } catch (FatalSessionException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException("Unexpected exception for session: " + settings.getLabel(), e);
        } finally {
            stateMonitor.stateChanged(PROBLEM);
        }
    }

    private void realIdle(Folder folder, long timeout) throws MessagingException {
        if (folder instanceof IMAPFolder) {
            // Before idling, schedule a call to interrupt the process after timeout
            ScheduledFuture<Integer> t = sharedPool.schedule(folder::getMessageCount, timeout, TimeUnit.MILLISECONDS);

            try {
                // Idle on the folder
                ((IMAPFolder)folder).idle();
            } finally {
                // Make sure the timer doesn't fire if it hasn't already
                t.cancel(false);
            }
        } else {
            throw new MessagingException("Folder is not an IMAPFolder");
        }
    }

    private void fakeIdle(Folder folder, long timeout) throws MessagingException {
        try {
            long ivl = settings.getInboxPollingInterval().map(Duration::toMillis).orElse(5000L);
            long now = System.currentTimeMillis();
            long outTime = now + timeout;
            do {
                Thread.sleep(Math.min(ivl, outTime));
                folder.getMessageCount();
                now = System.currentTimeMillis();
            } while (outTime > now);
        } catch (InterruptedException ignored) {}
    }

    private void idle(Folder folder) throws MessagingException {
        long timeout = settings.getBoxHandlerPollingInterval().map(Duration::toMillis).orElse(300000L);
        if (supportsIdle.isPresent()) {
            // We've already tried to idle, so follow the previous result.
            if (supportsIdle.get()) realIdle(folder, timeout); else fakeIdle(folder, timeout);
        } else {
            // Try once to idle.
            try {
                realIdle(folder, timeout);
                supportsIdle = Optional.of(true);
            } catch (MessagingException ex) {
                INFO("IDLE not supported: {}", ex.getMessage());
                supportsIdle = Optional.of(false);
                fakeIdle(folder, timeout);
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
