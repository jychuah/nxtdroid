package org.kealinghornets.nxtdroid.NXT.replies;

import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;

/**
 * A structure that parses a {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSGETSTATUS} reply packet
 */
public class LsGetStatusReply extends Reply {
    public byte status;
    public byte bytes_ready;

    /**
     * A constructor that parses a LSGETSTATUS reply packet
     * @param reply the reply packet
     */
    public LsGetStatusReply(byte[] reply) {
        status = reply[2];
        bytes_ready = reply[3];
    }

    /**
     * Returns all fields as an HTML line delimited string
     * @return A String
     */
    public String toString() {
        return "LsGetStatus " + ErrorCode.toString(status) + ", " + bytes_ready + " bytes ready";
    }
}