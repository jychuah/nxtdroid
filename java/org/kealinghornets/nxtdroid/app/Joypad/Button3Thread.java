package org.kealinghornets.nxtdroid.app.Joypad;

/**
 * Created by jchuah on 11/14/13.
 */
public class Button3Thread extends ButtonThread {
    public Button3Thread() {
        setNXTThreadName("3");
    }
    public void run() {
        reset();
    }
}
