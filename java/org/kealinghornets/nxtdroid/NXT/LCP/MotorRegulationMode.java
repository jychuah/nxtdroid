package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for motor regulation modes defined by the NXT Direct Command specification.
 */
public class MotorRegulationMode {
    public static final byte REGULATION_MODE_IDLE = 0;
    public static final byte REGULATION_MODE_MOTOR_SPEED = 1;
    public static final byte REGULATION_MODE_MOTOR_SYNC = 2;
    public static final byte UNKNOWN = -1;

    /**
     * Gets the name of a given motor regulation mode
     * @param regmode The regulation mode constant to translate
     * @return A String representing the regulation mode
     */
    public static String toString(byte regmode) {
        switch(regmode) {
            case REGULATION_MODE_IDLE : return "REGULATION_MODE_IDLE";
            case REGULATION_MODE_MOTOR_SPEED : return "REGULATION_MODE_MOTOR_SPEED";
            case REGULATION_MODE_MOTOR_SYNC : return "REGULATION_MODE_MOTOR_SYNC";
            default : return "UNKNOWN";
        }
    }
}
