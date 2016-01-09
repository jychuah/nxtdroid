package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;
import org.kealinghornets.nxtdroid.NXT.replies.LsReadReply;

/**
 * A class representing an I2C Ultrasonic Sensor.
 *
 * @see NXT#getUltrasonicSensor(int)
 * @see UltrasonicSensor#getDistance()
 */
public class UltrasonicSensor extends I2CSensor {

    static final byte[] CONTINUOUS_MEASUREMENT = {DEVICE_ADDRESS, 0x41, 0x02 };
    static final byte[] READ_MEASUREMENT_0 = { DEVICE_ADDRESS, 0x42 };

    static final String DISTANCE = "org.kealinghornets.nxtdroid.NXT.UltrasonicSensor.DISTANCE";
    boolean running = false;

    UltrasonicSensor(NXT nxt, int port) {
        super(nxt, port);
        startUltrasonicSensor();
    }

    /**
     * A wrapper method that starts an UltrasonicSensor sensor on continuous read mode on the specified port
     * @return An {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode} value
     */
    int startUltrasonicSensor() {
        int result = LsWrite(UltrasonicSensor.CONTINUOUS_MEASUREMENT, 0);
        if (result == ErrorCode.OK) {
            running = true;
        }
        return result;
    }

    @Override
    Bundle doSensorPoll() {
        if (!running) { return null; }
        LsWrite(UltrasonicSensor.READ_MEASUREMENT_0, 1);
        LsReadReply reply = LsRead();
        if (reply == null) { return null; }
        Bundle b = new Bundle();
        b.putInt(DISTANCE, reply.rx_data[0]);
        return b;
    }

    /**
     * Returns a distance value in centimeters. I think.
     *
     * @return A rough distance value, or -1 if nothing is in range
     */
    public int getDistance() {
        Bundle b = getValue();
        if (b == null) { nxt.addThreadEvent("Ultrasonic sensor Bundle result null"); return -1; }
        return b.getInt(DISTANCE);
    }

    /**
     * Determines whether the Ultrasonic Sensor started and is currently running continuous measurements
     *
     * @return true if it is running
     */
    public boolean isRunning() {
        return running;
    }
}
