package org.kealinghornets.nxtdroid.NXT.replies;

import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorType;

import de.waldheinz.fs.fat.LittleEndian;

/**
 * A structure that parses a {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#GETINPUTVALUES} reply packet
 */
public class SensorInputReply extends Reply {
    public byte status;
    public byte port;
    public boolean valid;
    public boolean calibrated;
    public byte sensor_type;
    public byte sensor_mode;
    public int raw_value;
    public int normalized_value;
    public short scaled_value;
    public short calibrated_value;

    /**
     * A constructor that parses a GETINPUTVALUES reply packet
     * @param reply the reply packet
     */
    public SensorInputReply(byte[] reply) {
        status = reply[2];
        port = reply[3];
        valid = reply[4] > 0;
        calibrated = reply[5] > 0;
        sensor_type = reply[6];
        sensor_mode = reply[7];
        raw_value = LittleEndian.getUInt16(reply, 8);
        normalized_value = LittleEndian.getUInt16(reply, 10);
        scaled_value = LittleEndian.getUInt16(reply, 12);
        calibrated_value = LittleEndian.getUInt16(reply, 14);
    }

    /**
     * Returns all fields as an HTML line delimited string
     * @return A String
     */
    public String toString() {
        return("SensorInputReply: <br />" +
                "** status " + ErrorCode.toString(status) + "<br />" +
                "** port " + port + "<br />" +
                "** valid " + (valid ? "TRUE" : "FALSE") + "<br />" +
                "** calibrated " + (calibrated ? "TRUE" : "FALSE") + "<br />" +
                "** sensor_type " + SensorType.toString(sensor_type) + "<br />" +
                "** sensor_mode " + SensorMode.toString(sensor_mode) + "<br />" +
                "** raw_value " + raw_value + "<br />" +
                "** normalized_value " + normalized_value + "<br />" +
                "** scaled_value " + scaled_value + "<br />" +
                "** calibrated_value " + calibrated_value + "<br />");
    }
}