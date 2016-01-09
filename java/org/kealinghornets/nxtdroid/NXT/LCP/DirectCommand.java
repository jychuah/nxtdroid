package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for OpCodes defined by the NXT Direct Command specification.
 */
public class DirectCommand {
    /**
     * An array of default command message lengths, by index of OpCode
     */
    public static final byte[] SendLength =  { 22, 2, 23, 6, 12, 5, 3,  3,  3, 63, 4, 2, 2, 2, 3, 21, 3, 2,  5 };
    /**
     * An array of default message reply lengths, by index of OpCode
     */
    public static final byte[] ReplyLength = { 0,  0, 0,  0, 0,  0, 25, 15, 0, 0,  0, 4, 0, 0, 3, 0, 19, 22, 63 };
    /**
     * An array that specifies whether an OpCode is a System command. Currently, no Direct Comamnd OpCodes are System commands
     */
    public static final boolean[] SystemCommand = { false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false  };

    public static final byte STARTPROGRAM = 0;
    public static final byte STOPPROGRAM = 1;
    public static final byte PLAYSOUNDFILE = 2;
    public static final byte PLAYTONE = 3;
    public static final byte SETOUTPUTSTATE = 4;
    public static final byte SETINPUTMODE = 5;
    public static final byte GETOUTPUTSTATE = 6;
    public static final byte GETINPUTVALUES = 7;
    public static final byte RESETINPUTSCALEDVALUE = 8;
    public static final byte MESSAGEWRITE = 9;
    public static final byte RESETMOTORPOSITION = 10;
    public static final byte GETBATTERYLEVEL = 11;
    public static final byte STOPSOUNDPLAYBACK = 12;
    public static final byte KEEPALIVE = 13;
    public static final byte LSGETSTATUS = 14;
    public static final byte LSWRITE = 15;
    public static final byte LSREAD = 16;
    public static final byte GETCURRENTPROGRAMNAME = 17;
    public static final byte MESSAGEREAD = 19;
    public static final byte UNKNOWN = -1;

    /**
     * Gets the name of a given OpCode
     * @param OpCode The OpCode to translate
     * @return A String constant with the name of the OpCode
     */
    public static String toString(byte OpCode) {
        switch (OpCode) {
            case STARTPROGRAM : return "STARTPROGRAM";
            case STOPPROGRAM : return "STOPPROGRAM";
            case PLAYSOUNDFILE : return "PLAYSOUNDFILE";
            case PLAYTONE : return "PLAYTONE";
            case SETOUTPUTSTATE : return "SETOUTPUTSTATE";
            case SETINPUTMODE : return "SETINPUTMODE";
            case GETOUTPUTSTATE : return "GETOUTPUTSTATE";
            case GETINPUTVALUES : return "GETINPUTVALUES";
            case RESETINPUTSCALEDVALUE : return "RESETINPUTSCALEDVALUE";
            case MESSAGEWRITE : return "MESSAGEWRITE";
            case RESETMOTORPOSITION : return "RESETMOTORPOSITION";
            case GETBATTERYLEVEL : return "GETBATTERYLEVEL";
            case STOPSOUNDPLAYBACK : return "STOPSOUNDPLAYBACK";
            case KEEPALIVE : return "KEEPALIVE";
            case LSGETSTATUS : return "LSGETSTATUS";
            case LSWRITE : return "LSWRITE";
            case LSREAD : return "LSREAD";
            case GETCURRENTPROGRAMNAME : return "GETCURRENTPROGRAMNAME";
            case MESSAGEREAD : return "MESSAGEREAD";
            default : return "UNKNOWN";
        }
    }
}
