package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for NXT motor modes defined by the NXT Direct Command specification.
 */
public class MotorMode {
    public static final byte MOTORON = 1;
    public static final byte BRAKE = 2;
    public static final byte REGULATED = 4;
    public static final byte UNKNOWN = -1;

    /**
     * Gets the name of a given motor mode
     * @param mode The mode to translate
     * @return A String representing the name of the motor mode
     */
    public static String toString(byte mode) {
        switch (mode) {
            case MOTORON : return "MOTORON";
            case BRAKE : return "BRAKE";
            case REGULATED : return "REGULATED";
            default : return "UNKNOWN";
        }
    }
}
