package spindlebox.util;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * spindlebox: MailUtils
 * Created by dboitnot on 10/11/14.
 */
public class MailUtils {
    public static void move(Message msg, Folder dest, boolean createDest) throws MessagingException {
        if (!dest.exists()) {
            if (createDest) {
                dest.create(Folder.HOLDS_MESSAGES);
            } else {
                throw new MessagingException("Destination folder doesn't exist: " + dest.getFullName());
            }
        }

        Message[] messages = { msg };
        Folder src = msg.getFolder();
        src.copyMessages(messages, dest);
        msg.setFlag(Flag.DELETED, true);
        src.expunge();
    }
}
