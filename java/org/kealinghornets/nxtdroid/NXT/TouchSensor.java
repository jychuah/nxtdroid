package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.SensorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorType;
import org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply;

/**
 * A class representing an NXT touch sensor. Because of Bluetooth latency, momentary presses
 * can be difficult to detect.
 *
 * @see NXT#getTouchSensor(int)
 * @see TouchSensor#isPressed()
 */
public class TouchSensor extends StandardSensor {
    static final String PRESSED = "org.kealinghornets.nxtdroid.NXT.TouchSensor.PRESSED";


    TouchSensor(NXT nxt, int port) {
        super(nxt, port, SensorType.SWITCH, SensorMode.BOOLEANMODE);
    }

    Bundle processSensorInputReply(SensorInputReply reply) {
        Bundle b = new Bundle();
        b.putBoolean(PRESSED, ((SensorInputReply)reply).scaled_value >= 1);
        return b;
    }

    /**
     * Gets whether or not the touch sensor is being pressed
     * @return true, if pressed
     */
    public boolean isPressed() {
        Bundle b = getValue();
        if (b == null) {
            return false;
        }
        return b.getBoolean(PRESSED);
    }
}
