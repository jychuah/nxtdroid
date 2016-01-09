package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.SensorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorType;
import org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply;

/**
 * A class representing an NXT Light Sensor. It can be either active or inactive. Active Light Sensors
 * have the LED turned on.
 *
 * @see NXT#getLightSensor(int)
 * @see NXT#getActiveLightSensor(int)
 * @see LightSensor#getLightValue()
 */
public class LightSensor extends StandardSensor {

    boolean active = false;
    static final String LIGHT_VALUE = "org.kealinghornets.nxtdroid.NXT.LightSensor.LIGHT_VALUE";


    LightSensor(NXT nxt, int port) {
        this(nxt, port, false);
    }

    LightSensor(NXT nxt, int port, boolean active) {
        super(nxt, port, active ? SensorType.LIGHT_ACTIVE : SensorType.LIGHT_INACTIVE, SensorMode.PCTFULLSCALEMODE);
        this.active = active;
    }

    @Override
    Bundle processSensorInputReply(SensorInputReply reply) {
        Bundle b = new Bundle();
        b.putInt(LIGHT_VALUE, reply.calibrated_value);
        return b;
    }

    /**
     * Returns true if the sensor is active (the LED on the sensor is on.)
     * @return true if the sensor is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets a light value. Higher values are brighter.
     *
     * @return A value between 0 and 1023, or -1 if the sensor could not be read
     */
    public int getLightValue() {
        Bundle b = getValue();
        if (b == null) return -1;
        return b.getInt(LIGHT_VALUE);
    }
}
