package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for sensor types defined by the NXT Direct Command specification.
 */
public class SensorType {
    public static final byte NO_SENSOR = 0;
    public static final byte SWITCH = 1;
    public static final byte TEMPERATURE = 2;
    public static final byte REFLECTION = 3;
    public static final byte ANGLE = 4;
    public static final byte LIGHT_ACTIVE = 5;
    public static final byte LIGHT_INACTIVE = 6;
    public static final byte SOUND_DB = 7;
    public static final byte SOUND_DBA = 8;
    public static final byte CUSTOM = 9;
    public static final byte LOWSPEED = 10;
    public static final byte LOWSPEED_9V = 11;
    public static final byte HIGHSPEED = 12;
    public static final byte COLORFULL = 13;
    public static final byte COLORRED = 14;
    public static final byte COLORGREEN = 15;
    public static final byte COLORBLUE = 16;
    public static final byte COLORNONE = 17;
    public static final byte UNKNOWN = -1;

    /**
     * Gets the name of a sensor type given a sensor type constant
     * @param sensortype The given sensor type constant
     * @return A String representing the sensor type
     */
    public static String toString(byte sensortype) {
        switch(sensortype) {
            case NO_SENSOR : return "NO_SENSOR";
            case SWITCH : return "SWITCH";
            case TEMPERATURE : return "TEMPERATURE";
            case REFLECTION : return "REFLECTION";
            case ANGLE : return "ANGLE";
            case LIGHT_ACTIVE : return "LIGHT_ACTIVE";
            case LIGHT_INACTIVE : return "LIGHT_INACTIVE";
            case SOUND_DB : return "SOUND_DB";
            case SOUND_DBA : return "SOUND_DBA";
            case CUSTOM : return "CUSTOM";
            case LOWSPEED : return "LOWSPEED";
            case LOWSPEED_9V : return "LOWSPEED_9V";
            case HIGHSPEED : return "HIGHSPEED";
            case COLORFULL : return "COLORFULL";
            case COLORRED : return "COLORRED";
            case COLORGREEN : return "COLORGREEN";
            case COLORBLUE : return "COLORBLUE";
            case COLORNONE : return "COLORNONE";
            default : return "UNKNOWN";
        }
    }
}
