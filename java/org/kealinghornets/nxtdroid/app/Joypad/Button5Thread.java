package org.kealinghornets.nxtdroid.app.Joypad;

/**
 * Created by jchuah on 11/14/13.
 */
public class Button5Thread extends ButtonThread {
    public Button5Thread() {
        setNXTThreadName("5");
    }
    public void run() {
        reset();
    }
}
