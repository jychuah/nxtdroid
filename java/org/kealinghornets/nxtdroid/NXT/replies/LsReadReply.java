package org.kealinghornets.nxtdroid.NXT.replies;

import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;

/**
 * A structure that parses an {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSREAD} reply packet
 */
public class LsReadReply extends Reply {
    public byte status;
    public byte bytesread;
    public byte[] rx_data = new byte[16];

    /**
     * A constructor that parses an LSREADREPLY reply packet
     * @param reply the reply packet
     */
    public LsReadReply(byte[] reply) {
        status = reply[2];
        bytesread = reply[3];
        for (int i = 0; i < 16; i++) {
            rx_data[i] = reply[i + 4];
        }
    }

    /**
     * Returns all fields as an HTML line delimited string
     * @return
     */
    public String toString() {
        String result = "LsReadReply: <br />" +
                "** status " + ErrorCode.toString(status) + "<br />" +
                "** bytesread " + bytesread + "<br />" +
                "** rx_data ";
        for (int i = 0; i < 16; i++) {
            result += rx_data[i] + " ";
        }
        return result;
    }
}
