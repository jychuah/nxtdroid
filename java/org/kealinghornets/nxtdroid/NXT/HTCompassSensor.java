package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.I2CSensor;
import org.kealinghornets.nxtdroid.NXT.replies.LsReadReply;

import de.waldheinz.fs.fat.LittleEndian;

/**
 * Created by jychuah on 11/16/13.
 */
public class HTCompassSensor extends I2CSensor {
    static final byte[] READ_HEADING = { DEVICE_ADDRESS, 0x44 };
    static final String HEADING = "org.kealinghornets.nxtdroid.NXT.HTCompassSensor.HEADING";
    public HTCompassSensor(NXT nxt, int port) {
        super(nxt, port);
    }

    @Override
    Bundle doSensorPoll() {
        LsWrite(READ_HEADING, 2);
        LsReadReply reply = LsRead();
        if (reply == null) { return null; }
        Bundle b = new Bundle();
        b.putInt(HEADING, LittleEndian.getUInt16(reply.rx_data, 0));
        return b;
    }

    public int getHeading() {
        Bundle b = getValue();
        if (b == null) { return -1; }
        return b.getInt(HEADING);

    }
}
