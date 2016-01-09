package org.kealinghornets.nxtdroid.NXT;

import android.util.Log;

import org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand;
import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorType;
import org.kealinghornets.nxtdroid.NXT.replies.LsGetStatusReply;
import org.kealinghornets.nxtdroid.NXT.replies.LsReadReply;

/**
 * An abstract base class for I2CSensor sensor classes
 */
public abstract class I2CSensor extends Sensor {
    boolean initialized = false;
    static final byte DEVICE_ADDRESS = 0x02;
    static final byte[] READ_SENSOR_TYPE = { DEVICE_ADDRESS, 0x10 };

    I2CSensor(NXT nxt, int port) {
        super(nxt, port, SensorType.LOWSPEED_9V, SensorMode.RAWMODE);
        initialized = initI2CSensor();
    }


    /**
     * A wrapper method that initializes and I2CSensor sensor on the specified port and clears out
     * the garbage LSREAD packets that it initially generates
     *
     * @return true if the sensor was successfully initialized, false if otherwise
     */
    boolean initI2CSensor() {
        LsGetStatusReply reply = LsGetStatus();
        if (reply.status != ErrorCode.OK) {
            Log.d("I2CSensor", ErrorCode.toString(reply.status));
            reply = LsGetStatus();
        }
        if (reply.status != ErrorCode.OK) {
            nxt.addThreadEvent("Could not get valid LsGetStatus from " + NXT.getInputName(port));
            return false;
        }
        int bytesleft = reply.bytes_ready;
        while (bytesleft > 0) {
            LsRead();
            bytesleft = bytesleft - 16;
        }
        reply = LsGetStatus();
        if (reply.status != ErrorCode.OK) {
            return false;
        }
        nxt.addThreadEvent("I2C init messages clear on " + NXT.getInputName(port));
        return true;
    }

    /**
     * Sends an {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSGETSTATUS} command to an
     * NXT and returns a {@link org.kealinghornets.nxtdroid.NXT.replies.LsGetStatusReply} instance
     * with the reply results.
     *
     * @return A new {@link org.kealinghornets.nxtdroid.NXT.replies.LsGetStatusReply} instance
     */
    LsGetStatusReply LsGetStatus() {
        NXTDroidCommand cmd = new NXTDroidCommand("I2C Sensor", DirectCommand.LSGETSTATUS, true, null);
        cmd.command[2] = (byte)port;
        nxt.send(cmd);
        waitForReply(cmd);
        return new LsGetStatusReply(cmd.reply);
    }

    /**
     * Sends a {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSWRITE} packet and returns
     * a reply status
     * @param txdata data to send
     * @param rxlength number of bytes required by the next {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSREAD}
     * @return An error code from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode} or {@link NXTDroidCommand#NO_REPLY}
     */
    int LsWrite(byte[] txdata, int rxlength) {
        NXTDroidCommand cmd = new NXTDroidCommand("I2C Sensor", DirectCommand.LSWRITE, true, null);
        cmd.cmdLength = txdata.length + 5;
        cmd.command[2] = (byte)port;
        cmd.command[3] = (byte)txdata.length;
        cmd.command[4] = (byte)rxlength;
        for (int i = 0; i < txdata.length; i++) {
            cmd.command[5 + i] = txdata[i];
        }
        nxt.send(cmd);
        return waitForReply(cmd);
    }

    /**
     * Sends a {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSREAD} packet and returns an instance of
     * {@link org.kealinghornets.nxtdroid.NXT.replies.LsReadReply} with the results
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.replies.LsReadReply}
     */
    LsReadReply LsRead() {
        NXTDroidCommand cmd = new NXTDroidCommand("I2C Sensor", DirectCommand.LSREAD, true, null);
        cmd.command[2] = (byte)port;
        nxt.send(cmd);
        byte result = waitForReply(cmd);
        if (result != ErrorCode.OK) { nxt.addThreadEvent("LsRead on " + NXT.getInputName(port) + " returned " + ErrorCode.toString(result));  Log.d("I2CSensor", ErrorCode.toString(result)); return null; }
        return new LsReadReply(cmd.reply);
    }

    /**
     * A wrapper method that gets a sensor type string. Don't know if this works for anything
     * other than the Ultrasonic Sensor
     *
     * @return "SONAR" if successful or null if unsuccessful
     */
    public String getSensorType() {
        if (!(LsWrite(READ_SENSOR_TYPE, 8) == ErrorCode.OK)) {
            return null;
        }
        LsReadReply reply = LsRead();
        if (reply == null) { return null; }
        if (reply.status == ErrorCode.OK) {
            String s = "";
            for (int i = 0; i < 8; i++) {
                s = s + (char)reply.rx_data[i];
            }
            return s;
        } else {
            return null;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

}

