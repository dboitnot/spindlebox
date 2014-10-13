package spindlebox.handler.box;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import static spindlebox.util.Logging.*;

/**
 * spindlebox: BoxHandler
 * Created by dboitnot on 10/11/14.
 */
public interface BoxHandler {
    public void process(Store store, Folder inbox) throws MessagingException;

    static <T extends BoxHandler> Module moduleFor(Class<T> cls) {
        return new AbstractModule() {
            @Override
            protected void configure() {
                DEBUG("Installing {}", cls);
                Multibinder<BoxHandler> binder = Multibinder.newSetBinder(binder(), BoxHandler.class);
                binder.addBinding().to(cls);
            }
        };
    }
}
