package spindlebox.ui;

public interface StateMonitor {
    public static enum State {
        DISCONNECTED, CONNECTING, PROCESSING, WAITING, PROBLEM
    }

    public void stateChanged(State newState);
}
