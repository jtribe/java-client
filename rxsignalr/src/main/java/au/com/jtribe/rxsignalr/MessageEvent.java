package au.com.jtribe.rxsignalr;

import com.google.gson.JsonElement;

/**
 * Created by angus on 8/09/2015.
 */
public final class MessageEvent {
    private final JsonElement json;


    MessageEvent(JsonElement json) {
        this.json = json;
    }
}
