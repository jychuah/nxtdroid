package org.kealinghornets.nxtdroid.app.Joypad;

import org.kealinghornets.nxtdroid.NXT.NXTThread;

public class Button1Thread extends ButtonThread {
    public Button1Thread() {
        setNXTThreadName("1");
    }

    public void run() {
        playTone(300, 1000);
        runMotor(PORT_C);
        wait(2000);
        stopMotor(PORT_C);
        reset();
    }
}
