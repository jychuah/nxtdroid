package org.kealinghornets.nxtdroid.app.Joypad;

/**
 * Created by jchuah on 11/14/13.
 */
public class Button2Thread extends ButtonThread {
    public Button2Thread() {
        setNXTThreadName("2");
    }
    public void run() {
        playTone(400, 1000);
        wait(3000);
        reset();
    }
}
