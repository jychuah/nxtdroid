package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for telegram types defined by the NXT Direct Command specification.
 */
public class Telegram {
    public static final byte DC_REPLY_REQUIRED = 0; // "Direct command telegram, response required"
    public static final byte SC_REPLY_REQUIRED = 1; // "System command telegram, response required"
    public static final byte REPLY = 2; // "Reply telegram"
    public static final byte DC_NO_REPLY = (byte)0x80; // "Direct command telegram, no response"
    public static final byte SC_NO_REPLY = (byte)0x81; // "System command telegram, no response"
    public static final byte UNKNOWN = (byte)-1; // "UNKNOWN"

    /**
     * Gets the description of a telegram type given its constant
     * @param telegramtype The telegram type to translate
     * @return A String with the description of the telegram
     */
    public static String toString(byte telegramtype) {
        switch (telegramtype) {
            case DC_REPLY_REQUIRED : return "Direct command telegram, response required";
            case SC_REPLY_REQUIRED : return "System command telegram, response required";
            case REPLY : return "Reply telegram";
            case DC_NO_REPLY : return "Direct command telegram, no response";
            case SC_NO_REPLY : return "System command telegram, no response";
            default : return "UNKNOWN";
        }
    }
}
