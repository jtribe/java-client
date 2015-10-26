package au.com.jtribe.rxsignalr;

/**
 * Created by angus on 9/09/2015.
 */
abstract class Event {
    private final String name;

    Event(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }
}
