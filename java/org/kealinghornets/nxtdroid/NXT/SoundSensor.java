package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.SensorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorType;
import org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply;

/**
 * A class representing an NXT sound sensor. It can be set to register either dB values or
 * dBA (human adjusted "loudness") values.
 *
 * @see NXT#getSoundSensordB(int)
 * @see NXT#getSoundSensordBA(int)
 * @see SoundSensor#getDecibels()
 */
public class SoundSensor extends StandardSensor {
    boolean dBA = false;

    static final String DECIBELS = "org.kealinghornets.nxtdroid.NXT.SoundSensor.DECIBELS";

    SoundSensor(NXT nxt, int port) {
        this(nxt, port, false);
    }

    SoundSensor(NXT nxt, int port, boolean dBA) {
        super(nxt, port, dBA ? SensorType.SOUND_DBA : SensorType.SOUND_DB, SensorMode.PCTFULLSCALEMODE);
        this.dBA = dBA;
    }

    @Override
    Bundle processSensorInputReply(SensorInputReply reply) {
        Bundle b = new Bundle();
        b.putInt(DECIBELS, reply.calibrated_value);
        return b;
    }

    /**
     * Get decibel value of detected sound
     *
     * @return A value between 0 and 1023
     */
    public int getDecibels() {
        Bundle b = getValue();
        if (b == null) return -1;
        return b.getInt(DECIBELS);
    }
}
