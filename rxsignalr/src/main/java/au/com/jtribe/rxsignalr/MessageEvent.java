package au.com.jtribe.rxsignalr;

import com.google.gson.JsonElement;
import org.json.JSONObject;

/**
 * Event that contains a JsonElement that was receieved from the SignalR pipeline
 */
public final class MessageEvent extends Event {
    public static final String NAME = "message";

    private final JsonElement json;

    MessageEvent(JsonElement json) {
        super(NAME);
        this.json = json;
    }

    public JsonElement json() {
        return json;
    }
}
