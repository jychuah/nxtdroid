package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand;
import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;
import org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply;

/**
 * An abstract passive analog NXT sensor class
 */
abstract class StandardSensor extends Sensor {

    StandardSensor(NXT nxt, int port, byte sensorType, byte sensorMode) {
        super(nxt, port, sensorType, sensorMode);
    }

    Bundle doSensorPoll() {
        SensorInputReply reply = getInputValues();
        if (reply == null) {
            return null;
        }
        return processSensorInputReply(reply);
    }

    /**
     * Sends a {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#GETINPUTVALUES} command and returns
     * an instance of {@link org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply} with the results
     * @return A new instance of {@link org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply} or <b>null</b> if a reply was not received
     */
    SensorInputReply getInputValues() {
        NXTDroidCommand cmd = new NXTDroidCommand("Get Input on " + nxt.getInputName(port), DirectCommand.GETINPUTVALUES, true, null);
        cmd.command[2] = (byte)port;
        nxt.send(cmd);
        int result = waitForReply(cmd);
        if (result == ErrorCode.OK) {
            return new SensorInputReply(cmd.reply);
        } else {
            return null;
        }
    }

    /**
     * Subclasses of {@link StandardSensor} should override this method
     * to determine how sensor reply values are interpreted, according to their specification
     *
     * @param reply A non-null instance of {@link org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply}
     * @return A {@link android.os.Bundle} with relevant sensor keys and information
     */
    abstract Bundle processSensorInputReply(SensorInputReply reply);
}
