package spindlebox.handler.box;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * spindlebox: BoxHandler
 * Created by dboitnot on 10/11/14.
 */
public interface BoxHandler {
    public void process(Store store, Folder inbox) throws MessagingException;
}
