package spindlebox;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

@ImplementedBy(AppControlImpl.class)
public interface AppControl {
    public void startup();

    public void shutdown(boolean userConfirm);

    default void shutdown() {
        shutdown(true);
    }
}
