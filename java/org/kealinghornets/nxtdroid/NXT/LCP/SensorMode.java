package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for Sensor Modes defined by the NXT Direct Command specification.
 */
public class SensorMode {
    public static final byte RAWMODE = (byte)0x00;
    public static final byte BOOLEANMODE = (byte)0x20;
    public static final byte TRANSITIONCNTMODE = (byte)0x40;
    public static final byte PERIODCOUNTERMODE = (byte)0x60;
    public static final byte PCTFULLSCALEMODE = (byte)0x80;
    public static final byte CELSIUSMODE = (byte)0xA0;
    public static final byte FAHRENHEITMODE = (byte)0xC0;
    public static final byte ANGLESTEPMODE = (byte)0xE0;
    public static final byte SLOPEMASK = (byte)0x1F;
    public static final byte MODEMASK = (byte)0xE0;
    public static final byte UNKNOWN = (byte)-1;

    /**
     * Gets the name of a sensor mode
     * @param sensormode The sensor mode constant to translate
     * @return A String with representing the sensor mode
     */
    public static String toString(byte sensormode) {
        switch(sensormode) {
            case RAWMODE : return "RAWMODE";
            case BOOLEANMODE : return "BOOLEANMODE";
            case TRANSITIONCNTMODE : return "TRANSITIONCNTMODE";
            case PERIODCOUNTERMODE : return "PERIODCOUNTERMODE";
            case PCTFULLSCALEMODE : return "PCTFULLSCALEMODE";
            case CELSIUSMODE : return "CELSIUSMODE";
            case FAHRENHEITMODE : return "FAHRENHEITMODE";
            case ANGLESTEPMODE : return "ANGLESTEPMODE";
            case SLOPEMASK : return "SLOPEMASK";
            default : return "UNKNOWN";
        }
    }
}
