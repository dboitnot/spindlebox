package spindlebox.ui;

public interface StateMonitor {
    public static enum State {
        DISCONNECTED, CONNECTING, PROCESSING, WAITING, PROBLEM, SHUTTING_DOWN
    }

    public void stateChanged(State newState);
}
