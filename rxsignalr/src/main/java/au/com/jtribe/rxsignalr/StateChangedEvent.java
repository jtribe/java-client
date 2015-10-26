package au.com.jtribe.rxsignalr;

/**
 * Created by angus on 8/09/2015.
 */
public class StateChangedEvent extends Event {
    public static final String NAME = "state_changed";


    public enum State {
        CONNECTING, CONNECTED, RECONNECTING, DISCONNECTED, INITIAL_CONNECTION
    }

    private final State newState;
    private final State oldState;

    public StateChangedEvent(State oldState, State newState) {
        super(NAME);
        this.oldState = oldState;
        this.newState = newState;
    }

    public static final StateChangedEvent INITIAL_CONNECTION = new StateChangedEvent(State.INITIAL_CONNECTION, State.INITIAL_CONNECTION);

    @Override
    public String toString() {
        return oldState.toString() + " -> " + newState.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateChangedEvent that = (StateChangedEvent) o;

        if (newState != that.newState) return false;
        return oldState == that.oldState;
    }

    @Override
    public int hashCode() {
        int result = newState.hashCode();
        result = 31 * result + oldState.hashCode();
        return result;
    }
}
