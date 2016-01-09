package org.kealinghornets.nxtdroid.app.Joypad;

/**
 * Created by jchuah on 11/14/13.
 */
public class Button6Thread extends ButtonThread {
    public Button6Thread(){
        setNXTThreadName("6");
    }

    public void run() {
        reset();
    }
}
