package au.com.jtribe.rxsignalr;

import com.google.gson.JsonElement;

import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by angus on 8/09/2015.
 */
final class MessageEventOnSubscribe implements Observable.OnSubscribe<MessageEvent> {
    private final Connection connection;

    MessageEventOnSubscribe(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void call(final Subscriber<? super MessageEvent> subscriber) {
        connection.received(new MessageReceivedHandler() {
            @Override
            public void onMessageReceived(JsonElement json) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(new MessageEvent(json));
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
}
