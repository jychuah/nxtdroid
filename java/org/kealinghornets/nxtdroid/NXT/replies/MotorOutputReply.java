package org.kealinghornets.nxtdroid.NXT.replies;

import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;
import org.kealinghornets.nxtdroid.NXT.LCP.MotorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.MotorRegulationMode;
import org.kealinghornets.nxtdroid.NXT.LCP.MotorRunState;
import org.kealinghornets.nxtdroid.NXT.NXT;

import de.waldheinz.fs.fat.LittleEndian;

/**
 * A structure that parses a {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#GETOUTPUTSTATE} reply packet
 */
public class MotorOutputReply extends Reply {
    public byte status;
    public byte port;
    public byte power;
    public byte mode;
    public byte reg_mode;
    public byte turn_ratio;
    public byte run_state;
    public int tachometer_limit;
    public int system_tachometer;
    public int tachometer;
    public int rotation_count;

    /**
     * A constructor that parses a GETOUTPUTSTATE reply packet
     * @param reply the reply packet
     */
    public MotorOutputReply(byte[] reply) {
        status = reply[2];
        if (status == ErrorCode.OK) {
            port = reply[3];
            power = reply[4];
            mode = reply[5];
            reg_mode = reply[6];
            turn_ratio = reply[7];
            run_state = reply[8];
            tachometer_limit = LittleEndian.getUInt32(reply, 9);
            system_tachometer = LittleEndian.getUInt32(reply, 13);
            tachometer = LittleEndian.getUInt32(reply, 17);
            rotation_count = LittleEndian.getUInt32(reply, 21);
        }
    }

    /**
     * Returns all fields and values as an HTML line delimited string
     * @return A String
     */
    public String toString() {
        return("MotorOutputReply: <br />" +
                "** status " + ErrorCode.toString(status) + "<br />" +
                "** port " +  NXT.getMotorName(port) + "<br />" +
                "** power " + power + "<br />" +
                "** mode " + MotorMode.toString(mode) + "<br />" +
                "** reg_mode " + MotorRegulationMode.toString(reg_mode) + "<br />" +
                "** turn_ratio " + turn_ratio + "<br />" +
                "** run_state " + MotorRunState.toString(run_state) + "<br />" +
                "** tachometer_limit " + tachometer_limit + "<br />" +
                "** system_tachometer " + system_tachometer + "<br />" +
                "** tachometer " + tachometer + "<br />" +
                "** rotation_count " + rotation_count);
    }
}