package org.kealinghornets.nxtdroid.app.Joypad;

import android.util.Log;

import org.kealinghornets.nxtdroid.NXT.NXTThread;

/**
 * Created by jychuah on 11/13/13.
 */
public class JoypadNXTThread extends NXTThread {
    public boolean running = true;
    JoypadView joypad;
    public void setup() {
        setLogLevel(NXTThread.DEBUG);
    }
    public void run() {

        int lastLeft = 0;
        int lastRight = 0;

        setNXTThreadName("Joypad Thread");
        while (running) {
            double x = joypad.getJoypadX();
            double y = joypad.getJoypadY();

            if (Math.abs(x) < 0.2) {
                x = 0;

            }
            if (Math.abs(y) < 0.2) {
                y = 0;
            }

            int left = (int)(y * 100);
            int right = (int)(y * 100);
            left = left + (int)((x * (1 - Math.abs(y) / 2)) * 100);
            right = right - (int)((x * (1 - Math.abs(y) / 2)) * 100);

            if (left > 100) {
                left = 100;
            }
            if (left < -100) {
                left = -100;
            }
            if (right > 100) {
                right = 100;
            }
            if (right < -100) {
                right = -100;
            }
            if (left != lastLeft || right != lastRight) {
                lastLeft = left;
                lastRight = right;
                runMotor(PORT_1, left);
                runMotor(PORT_2, right);
            }
            wait(50);
        }
        end();
    }
}
