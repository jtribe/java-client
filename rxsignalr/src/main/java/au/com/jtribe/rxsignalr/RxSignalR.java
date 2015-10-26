package au.com.jtribe.rxsignalr;

import android.util.Log;
import microsoft.aspnet.signalr.client.Connection;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.NullLogger;
import microsoft.aspnet.signalr.client.transport.AutomaticTransport;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.LongPollingTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;
import rx.Observable;
import rx.functions.Func1;

public final class RxSignalR {

  public static class Builder {
    private Credentials credentials;
    private String url;
    private Logger logger = new NullLogger();
    private ClientTransport transport;

    public Builder credentials(Credentials credentials) {
      this.credentials = credentials;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder serverSentEvents() {
      this.transport = new ServerSentEventsTransport(logger);
      return this;
    }

    public Builder longPolling() {
      this.transport = new LongPollingTransport(logger);
      return this;
    }

    public Builder automatic() {
      this.transport = new AutomaticTransport(logger);
      return this;
    }

    public RxSignalR create() {
      return new RxSignalR(url, credentials, this.transport, logger);
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
              Log.v("RxSignalR", s);
              break;
          }
        }
      };
      return this;
    }
  }

  private final Observable<StateChangedEvent> stateChangedEventObservable;
  private final Observable<MessageEvent> messageEventObservable;
  private final Connection connection;

  RxSignalR(String url, Credentials credentials, ClientTransport transport, Logger logger) {
    connection = new Connection(url, logger);
    connection.setCredentials(credentials);

    Log.v("RxSignalR", "Initial Connection State: " + connection.getState().toString());

    this.stateChangedEventObservable =
        Observable.create(new StateChangedEventOnSubscribe(connection, transport)).share();

    this.messageEventObservable =
        this.stateChangedEventObservable.filter(new Func1<StateChangedEvent, Boolean>() {
          @Override
          public Boolean call(StateChangedEvent stateChangedEvent) {
            return stateChangedEvent == StateChangedEvent.INITIAL_CONNECTION;
          }
        }).flatMap(new Func1<StateChangedEvent, Observable<MessageEvent>>() {
          @Override
          public Observable<MessageEvent> call(StateChangedEvent stateChangedEvent) {
            return Observable.create(new MessageEventOnSubscribe(connection)).share();
          }
        });
  }

  public Observable<MessageEvent> message() {
    return this.messageEventObservable;
  }

  public Observable<StateChangedEvent> stateChangedEvent() {
    return this.stateChangedEventObservable;
  }

  public void send(String json) {
    connection.send(json);
  }
}
