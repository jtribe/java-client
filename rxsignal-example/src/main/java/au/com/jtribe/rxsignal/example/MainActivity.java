package au.com.jtribe.rxsignal.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.java_websocket.WebSocketImpl;

import au.com.jtribe.rxsignalr.MessageEvent;
import au.com.jtribe.rxsignalr.RxSignalR;
import au.com.jtribe.rxsignalr.StateChangedEvent;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        final RxSignalR signalR = new RxSignalR.Builder()
                .url("url")
                .credentials(new RxCredentials())
                .logging()
                .create();
        
        signalR.message()
                .subscribe(new Action1<MessageEvent>() {
                    @Override
                    public void call(MessageEvent messageEvent) {
                        Log.d("MainActivity", "Message: " + messageEvent.toString());

                    }
                });
    }

    static class RxCredentials implements Credentials {

        @Override
        public void prepareRequest(Request request) {
            request.addHeader("Authorization", "Bearer ");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
