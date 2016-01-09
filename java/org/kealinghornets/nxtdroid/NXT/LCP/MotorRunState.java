package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Constants for motor run states defined by the NXT Direct Command specification.
 *
 */
public class MotorRunState {
    public static final byte MOTOR_RUN_STATE_IDLE = 0;
    public static final byte MOTOR_RUN_STATE_RAMPUP = (byte)0x10;
    public static final byte MOTOR_RUN_STATE_RUNNING = (byte)0x20;
    public static final byte MOTOR_RUN_STATE_RAMPDOWN = (byte)0x40;
    public static final byte UNKNOWN = -1;

    /**
     * Gets the name of a given motor run state
     * @param runstate A run state to translate
     * @return A String representing the motor run state
     */
    public static String toString(byte runstate) {
        switch(runstate) {
            case MOTOR_RUN_STATE_IDLE: return "MOTOR_RUN_STATE_IDLE";
            case MOTOR_RUN_STATE_RAMPUP: return "MOTOR_RUN_STATE_RAMPUP";
            case MOTOR_RUN_STATE_RUNNING : return "MOTOR_RUN_STATE_RUNNING";
            case MOTOR_RUN_STATE_RAMPDOWN : return "MOTOR_RUN_STATE_RAMPDOWN";
            default : return "UNKNOWN";
        }
    }
}
