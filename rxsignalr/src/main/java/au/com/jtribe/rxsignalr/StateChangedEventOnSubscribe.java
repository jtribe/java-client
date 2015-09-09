package au.com.jtribe.rxsignalr;

import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by angus on 8/09/2015.
 */
final class StateChangedEventOnSubscribe implements Observable.OnSubscribe<StateChangedEvent> {
    private final Connection connection;

    StateChangedEventOnSubscribe(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void call(final Subscriber<? super StateChangedEvent> subscriber) {
        this.connection.stateChanged(new StateChangedCallback() {
            @Override
            public void stateChanged(ConnectionState oldState, ConnectionState newState) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new StateChangedEvent(stateFromConnectionState(oldState), stateFromConnectionState(newState)));
                }
            }
        });

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                connection.stateChanged(null);
            }
        }));
    }

    private StateChangedEvent.State stateFromConnectionState(ConnectionState state) {
        switch (state) {
            case Connecting:
                return StateChangedEvent.State.CONNECTING;
            case Connected:
                return StateChangedEvent.State.CONNECTED;
            case Reconnecting:
                return StateChangedEvent.State.RECONNECTING;
            case Disconnected:
                return StateChangedEvent.State.DISCONNECTED;
            default:
                throw new IllegalArgumentException("ConnectionState was not one of Connecting, Connected, Reconnecting, or Disconnected");
        }
    }
}
