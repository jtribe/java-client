package au.com.jtribe.rxsignalr;

import android.util.Log;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.NullLogger;
import rx.Observable;

public final class RxSignalR {

    public static class Builder {
        private Credentials credentials;
        private String url;
        private Logger logger = new NullLogger();

        public Builder credentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public RxSignalR create() {
            return new RxSignalR(url, credentials, logger);
        }

        public Builder logging() {
            this.logger = new Logger() {

                @Override
                public void log(String s, LogLevel logLevel) {
                    switch (logLevel) {
                        case Critical:
                            Log.e("RxSignalR", s);
                            break;
                        case Information:
                            Log.i("RxSignalR", s);
                            break;
                        case Verbose:
                            Log.d("RxSignalR", s);
                            break;
                    }
                }
            };
            return this;
        }
    }

    private final Observable<StateChangedEvent> stateChangedEventObservable;
    private final Observable<MessageEvent> messageEventObservable;

    RxSignalR(String url, Credentials credentials, Logger logger) {
        final Connection connection = new Connection(url, logger);
        connection.setCredentials(credentials);

        Log.d("RxSignalR", "Initial Connection State: " + connection.getState().toString());

        this.stateChangedEventObservable = Observable.create(new StateChangedEventOnSubscribe(connection)).share();
        this.messageEventObservable = Observable.create(new MessageEventOnSubscribe(connection)).share();

        connection.start().done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                Log.d("RxSignalR", "Started SignalR connection");
                Log.d("RxSignalR", "Connected Connection State: " + connection.getState().toString());
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                Log.e("RxSignalR", "Error while connecting", throwable);
            }
        });
    }




    public Observable<MessageEvent> message() {
        return this.messageEventObservable;
    }

    public Observable<StateChangedEvent> stateChangedEvent() {
        return this.stateChangedEventObservable;
    }
}
