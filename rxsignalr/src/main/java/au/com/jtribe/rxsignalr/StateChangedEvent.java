package au.com.jtribe.rxsignalr;

/**
 * Created by angus on 8/09/2015.
 */
public class StateChangedEvent {

    public enum State {
        CONNECTING, CONNECTED, RECONNECTING, DISCONNECTED
    }

    private final State newState;
    private final State oldState;

    public StateChangedEvent(State oldState, State newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public String toString() {
        return oldState.toString() + " -> " + newState.toString();
    }
}
