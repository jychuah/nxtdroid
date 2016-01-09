package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.SensorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.SensorType;
import org.kealinghornets.nxtdroid.NXT.replies.SensorInputReply;

/**
 * A class representing an NXT 2.0 Color Sensor. (NOT a HI-TECHNIC I2C COLOR SENSOR.)
 *
 * @see NXT#getColorSensor(int)
 * @see ColorSensor#getDetectedColor()
 */
public class ColorSensor extends StandardSensor {
    static final String DETECTED_COLOR = "org.kealinghornets.nxtdroid.NXT.sensor.DETECTED_COLOR";

    public static final String BLACK = "Black";
    public static final String BLUE = "Blue";
    public static final String GREEN = "Green";
    public static final String YELLOW = "Yellow";
    public static final String RED = "Red";
    public static final String WHITE = "White";

    ColorSensor(NXT nxt, int port) {
        super(nxt, port, SensorType.COLORFULL, SensorMode.RAWMODE);
    }

    @Override
    Bundle processSensorInputReply(SensorInputReply reply) {
        Bundle b = new Bundle();
        switch(reply.scaled_value) {
            case 1 : b.putString(DETECTED_COLOR, BLACK); break;
            case 2 : b.putString(DETECTED_COLOR, BLUE); break;
            case 3 : b.putString(DETECTED_COLOR, GREEN); break;
            case 4 : b.putString(DETECTED_COLOR, YELLOW); break;
            case 5 : b.putString(DETECTED_COLOR, RED); break;
            case 6 : b.putString(DETECTED_COLOR, WHITE); break;
            default : b.putString(DETECTED_COLOR, "UNKNOWN");
        }
        return b;
    }

    /**
     * Returns a string with the detected color, or "UNKNOWN" in the case of an error.
     *
     * @return {@link org.kealinghornets.nxtdroid.NXT.ColorSensor#BLACK} {@link org.kealinghornets.nxtdroid.NXT.ColorSensor#BLUE}
     *          {@link org.kealinghornets.nxtdroid.NXT.ColorSensor#GREEN} {@link org.kealinghornets.nxtdroid.NXT.ColorSensor#YELLOW}
     *          {@link org.kealinghornets.nxtdroid.NXT.ColorSensor#RED} {@link org.kealinghornets.nxtdroid.NXT.ColorSensor#WHITE} or
     *          "UNKNOWN" if a sensor error occurred
     */
    public String getDetectedColor() {
        Bundle b = getValue();
        if (b == null) { return "UNKNOWN"; }
        return b.getString(DETECTED_COLOR);
    }
}

