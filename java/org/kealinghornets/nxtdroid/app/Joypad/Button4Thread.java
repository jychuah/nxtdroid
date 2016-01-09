package org.kealinghornets.nxtdroid.app.Joypad;

/**
 * Created by jchuah on 11/14/13.
 */
public class Button4Thread extends ButtonThread {
    public Button4Thread() {
        setNXTThreadName("4");
    }

    public void run() {
        reset();
    }
}
