package au.com.jtribe.rxsignalr;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by angus on 8/09/2015.
 */
final class StateChangedEventOnSubscribe implements Observable.OnSubscribe<StateChangedEvent> {
    private final Connection connection;
    private final ClientTransport transport;

    StateChangedEventOnSubscribe(Connection connection, ClientTransport transport) {
        this.connection = connection;
        this.transport = transport;
    }

    @Override
    public void call(final Subscriber<? super StateChangedEvent> subscriber) {
        final SignalRFuture future = connection.start(this.transport)
                .done(new Action<Void>() {
                    @Override
                    public void run(Void obj) throws Exception {
                        subscriber.onNext(StateChangedEvent.INITIAL_CONNECTION);

                        connection.stateChanged(new StateChangedCallback() {
                            @Override
                            public void stateChanged(ConnectionState oldState, ConnectionState newState) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onNext(new StateChangedEvent(stateFromConnectionState(oldState), stateFromConnectionState(newState)));
                                }
                            }
                        });

                        connection.error(new ErrorCallback() {
                            @Override
                            public void onError(Throwable error) {
                                if (!subscriber.isUnsubscribed()) {
                                    subscriber.onError(error);
                                }
                            }
                        });
                    }
                }).onError(new ErrorCallback() {
                    @Override
                    public void onError(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                connection.stop();

                connection.stateChanged(null);
                connection.error(null);
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
