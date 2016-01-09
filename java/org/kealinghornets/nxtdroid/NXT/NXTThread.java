package org.kealinghornets.nxtdroid.NXT;

import org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand;
import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;
import org.kealinghornets.nxtdroid.NXT.LCP.MotorMode;
import org.kealinghornets.nxtdroid.NXT.LCP.MotorRegulationMode;
import org.kealinghornets.nxtdroid.NXT.LCP.MotorRunState;
import org.kealinghornets.nxtdroid.NXT.replies.MotorOutputReply;

import de.waldheinz.fs.fat.LittleEndian;

/**
 * An instruction thread class with wrappers for common NXT commands, such as running motors
 * or getting sensor input. By overriding {@link Thread#run()} a sequence of instructions
 * for an NXT can be specified.
 *
 * This class also registers itself with the {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager}
 * to provide application level thread monitoring.
 */
public class NXTThread extends Thread {
    NXT nxt;


    String id = "-1";
    private String name = "NXT Thread";
    private NXTDroidCommand lastCommand = null;
    private int debugLevel = LOG;
    int systemSleepTime = 40;
    int state = DISCONNECTED;
    private float scaleA = 1, scaleB = 1, scaleC = 1;
    private int differenceA = 0, differenceB = 0, differenceC = 0;

    /**
     * Gets the current motor scaling for motor A.
     *
     * @return The current scale factor
     */
    public float getScaleA() {
        return scaleA;
    }

    /**
     * Sets the current motor scaling for motor A. Any power levels set by {@link org.kealinghornets.nxtdroid.NXT.NXTThread#runMotor(int, int, boolean)}
     * are scaled and difference compensated before a final output power is sent to the NXT. The final power value is equal to scale * input + difference.
     * The default scale value is 1.0.
     *
     * @param scaleA
     */
    public void setScaleA(float scaleA) {
        this.scaleA = scaleA;
    }

    /**
     * Gets the current motor scaling for motor B.
     *
     * @return The current scale factor
     */
    public float getScaleB() {
        return scaleB;
    }

    /**
     * Sets the current motor scaling for motor B. Any power levels set by {@link org.kealinghornets.nxtdroid.NXT.NXTThread#runMotor(int, int, boolean)}
     * are scaled and difference compensated before a final output power is sent to the NXT. The final power value is equal to scale * input + difference.
     * The default scale value is 1.0.
     *
     * @param scaleB
     */
    public void setScaleB(float scaleB) {
        this.scaleB = scaleB;
    }


    /**
     * Gets the current motor scaling for motor C.
     *
     * @return The current scale factor
     */
    public float getScaleC() {
        return scaleC;
    }


    /**
     * Sets the current motor scaling for motor C. Any power levels set by {@link org.kealinghornets.nxtdroid.NXT.NXTThread#runMotor(int, int, boolean)}
     * are scaled and difference compensated before a final output power is sent to the NXT. The final power value is equal to scale * input + difference.
     * The default scale value is 1.0.
     *
     * @param scaleC
     */
    public void setScaleC(float scaleC) {
        this.scaleC = scaleC;
    }

    /**
     * Gets the current motor differencing for motor A.
     *
     * @return The current difference value
     */
    public int getDifferenceA() {
        return differenceA;
    }

    /**
     * Sets the current motor differencing for motor A. Any power levels set by {@link org.kealinghornets.nxtdroid.NXT.NXTThread#runMotor(int, int, boolean)}
     * are scaled and difference compensated before a final output power is sent to the NXT. The final power value is equal to scale * input + difference.
     * The default scale value is 1.0.
     *
     * @param differenceA
     */
    public void setDifferenceA(int differenceA) {
        this.differenceA = differenceA;
    }

    /**
     * Gets the current motor differencing for motor B.
     *
     * @return The current difference value
     */
    public int getDifferenceB() {
        return differenceB;
    }

    /**
     * Sets the current motor differencing for motor B. Any power levels set by {@link org.kealinghornets.nxtdroid.NXT.NXTThread#runMotor(int, int, boolean)}
     * are scaled and difference compensated before a final output power is sent to the NXT. The final power value is equal to scale * input + difference.
     * The default scale value is 1.0.
     *
     * @param differenceB
     */
    public void setDifferenceB(int differenceB) {
        this.differenceB = differenceB;
    }

    /**
     * Gets the current motor differencing for motor C.
     *
     * @return The current difference value
     */
    public int getDifferenceC() {
        return differenceC;
    }

    /**
     * Sets the current motor differencing for motor C. Any power levels set by {@link org.kealinghornets.nxtdroid.NXT.NXTThread#runMotor(int, int, boolean)}
     * are scaled and difference compensated before a final output power is sent to the NXT. The final power value is equal to scale * input + difference.
     * The default scale value is 1.0.
     *
     * @param differenceC
     */
    public void setDifferenceC(int differenceC) {
        this.differenceC = differenceC;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        NXTThread newThread = null;

        try {
            newThread = this.getClass().newInstance();
            newThread.nxt = this.nxt;
            newThread.name = this.name;
            newThread.debugLevel = this.debugLevel;
            newThread.logString = this.logString;
            newThread.systemSleepTime= this.systemSleepTime;
            newThread.state = NXTThread.READY;
        } catch (InstantiationException e) {
            log("Could not clone this thread. Do you have parameters in your thread constructor?");
        } finally {
            return newThread;
        }
    }

    /**
     * Returns the thread's ID, assigned by {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager}
     *
     * @return A String
     * @see org.kealinghornets.nxtdroid.NXT.NXTThreadManager
     */
    public String getNXTThreadID() {
        return id;
    }

    /**
     * Last time since {@link NXTThread#log(String, int)} was called
     */
    public long lastUpdateTime = 0;

    /**
     * Thread state: NXT has been disconnected
     */
    public static final int DISCONNECTED = -1;

    /**
     * Thread state: finished executing
     */
    public static final int STOPPED = 0;

    /**
     * Thread state: currently running
     */
    public static final int RUNNING = 2;

    /**
     * Thread state: ready to execute
     */
    public static final int READY = 3;

    /**
     * Motor output and encoder Port A
     */
    public static final byte PORT_A = NXT.PORT_A;

    /**
     * Motor output and encoder Port B
     */
    public static final byte PORT_B = NXT.PORT_B;

    /**
     * Motor output and encoder Port C
     */
    public static final byte PORT_C = NXT.PORT_C;

    /**
     * Sensor input Port 1
     */
    public static final byte PORT_1 = NXT.PORT_1;

    /**
     * Sensor input Port 2
     */
    public static final byte PORT_2 = NXT.PORT_2;

    /**
     * Sensor input Port 3
     */
    public static final byte PORT_3 = NXT.PORT_3;

    /**
     * Sensor input Port 4
     */
    public static final byte PORT_4 = NXT.PORT_4;

    /**
     * Debug level: no debugging
     */
    public static final int NONE = -1;

    /**
     * Debug level: wrapper commands only
     */
    public static final int LOG = 0;

    /**
     * Debug level: Direct Command calls
     */
    public static final int DEBUG = 1;

    /**
     * Debug level: Reply packet status results
     */
    public static final int VERBOSE = 2;

    /**
     * Log string, updated after calls to {@link NXTThread#log(String, int)}
     */
    public String logString = "";
    Object lock = new Object();

    /**
     * Gets the NXT associated with this NXTThread
     *
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.NXT}
     */
    public NXT getNXT() {
        return nxt;
    }

    /**
     * Sets which NXT this NXTThread will send commands to. Also sets the system sleep time
     * to the NXT's send delay + 5
     *
     * @param nxt An instance of {@link org.kealinghornets.nxtdroid.NXT.NXT}
     */
    public void setNXT(NXT nxt) {
        this.nxt = nxt;
        setSystemSleepTime(nxt.getSendDelay() + 5);
        state = READY;
    }

    /**
     * Gets current state of the thread
     * @return {@link NXTThread#RUNNING} {@link NXTThread#READY} {@link NXTThread#STOPPED}
     */
    public int getNXTThreadState() {
        return state;
    }


    /**
     * Default constructor - registers this thread with {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager}
     */
    public NXTThread() {
        NXTThreadManager.addItem(this);
    }

    /**
     * Sets the log level of events to record
     * @param level {@link NXTThread#NONE} {@link NXTThread#LOG} {@link NXTThread#DEBUG} {@link NXTThread#VERBOSE}
     */
    public void setLogLevel(int level) {
        debugLevel = level;
    }

    /**
     * Gets the currently executing {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand} instance
     *
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand} or <b>null</b> if none are executing
     */
    public NXTDroidCommand getLastCommand() {
        return lastCommand;
    }

    /**
     * Causes the thread to sleep for a short time. This method is called after Direct Command send transmissions
     * to prevent the send queue from overfilling
     *
     * @see org.kealinghornets.nxtdroid.NXT.NXTThread#getSystemSleepTime()
     * @see NXTThread#setSystemSleepTime(int)
     */
    public void systemSleep() {
        try {
            sleep(systemSleepTime);
        } catch (Exception e) {

        }
    }

    /**
     * Gets the amount of time this thread will sleep for during a {@link org.kealinghornets.nxtdroid.NXT.NXTThread#systemSleep()}
     * call
     *
     * @return A time in milliseconds
     */
    public int getSystemSleepTime() {
        return systemSleepTime;
    }

    /**
     * Sets the amount of time this thread will sleep for duing a {@link org.kealinghornets.nxtdroid.NXT.NXTThread#systemSleep()}
     * call. The default value is 40.
     *
     * @param systemSleepTime A time in milliseconds
     */
    public void setSystemSleepTime(int systemSleepTime) {
        this.systemSleepTime = systemSleepTime;
    }


    /**
     * Add a log entry at the {@link NXTThread#LOG} level
     * @param s The log entry to add
     */
    public void log(String s) {
        log(s, LOG);
    }

    /**
     * Add a log entry at the specified level
     * @param s The log entry to add
     * @param level {@link NXTThread#NONE} {@link NXTThread#LOG} {@link NXTThread#DEBUG} {@link NXTThread#VERBOSE}
     */
    public void log(String s, int level) {
        if (level <= debugLevel) {
            logString += s + "<br />";
            lastUpdateTime = System.currentTimeMillis();
            NXTThreadManager.notifyHandlers();
        }
    }
    /**
     * Returns the name of this thread, "NXT Thread" if not previously set with
     * {@link NXTThread#setNXTThreadName(String)}
     *
     * @return A String
     * @see NXTThread#setNXTThreadName(String)
     */
    public String getNXTThreadName() {
        return name;
    }

    /**
     * Sets the name of this NXTThread
     * @param name Name of the thread
     * @see org.kealinghornets.nxtdroid.NXT.NXTThread#getNXTThreadName()
     */
    public void setNXTThreadName(String name) {
        this.name = name;
        log("Thread name is now " + name, LOG);
    }

    /**
     * Wrapper method that rotates the motor attached to the specified port a number of
     * degrees at highest possible speed. It will be fairly inaccurate and overshoot significantly
     * before stopping. This method does not block execution until the movement is complete.
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param degrees Number of degrees to turn, positive or negative
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int rotateMotor(int port, int degrees) {
        return rotateMotor(port, degrees, 100, false);
    }

    /**
     * Wrapper method that rotates the motor attached to the specified port a number of degrees
     * at the specified speed. It can require a reply packet from the NXT. Lower speeds for
     * small degree turns are more accurate. This method does not block execution until the
     * movement is complete.
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param degrees Degrees, either positive or negative
     * @param speed A speed between 0 and 100. The method will auto select the direction.
     * @param reply true, if a reply status is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return {@link NXTDroidCommand#REPLY_COMPLETE} {@link NXTDroidCommand#RECEIVE_ERROR}
     *          {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int rotateMotor(int port, int degrees, int speed, boolean reply) {
        log("Rotating " + getMotorName(port) + " " + degrees + "degrees");
        int setpoint = (degrees > 0) ? Math.abs(speed) : -Math.abs(speed); // polarity reversed?
        return setOutputState(PORT_A, (byte) setpoint, MotorMode.MOTORON + MotorMode.BRAKE, MotorRegulationMode.REGULATION_MODE_MOTOR_SPEED, 0, MotorRunState.MOTOR_RUN_STATE_RUNNING, Math.abs(degrees), false);
    }

    /**
     * Wrapper method that applies braking to the specified motor for an immediate stop.
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int brakeMotor(int port) {
        return brakeMotor(port, false);
    }

    /**
     * Wrapper method that applies braking to the specified motor for an immediate stop. A reply
     * packet can be required.
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param reply true, if a reply status is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return {@link NXTDroidCommand#REPLY_COMPLETE} {@link NXTDroidCommand#RECEIVE_ERROR}
     *          {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int brakeMotor(int port, boolean reply) {
        log("Braking " + getMotorName(port), LOG);
        setOutputState(port, -nxt.port_speeds[port] / 2, MotorMode.MOTORON, MotorRegulationMode.REGULATION_MODE_MOTOR_SPEED, 0, MotorRunState.MOTOR_RUN_STATE_RUNNING, 0, reply);
        return setOutputState(port, 0, MotorMode.BRAKE, MotorRegulationMode.REGULATION_MODE_MOTOR_SPEED, 0, MotorRunState.MOTOR_RUN_STATE_RUNNING, 0, reply);
    }

    /**
     * Wrapper method that stops motor output to the specified port and allows it to coast
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int stopMotor(int port) {
        return stopMotor(port, false);
    }

    /**
     * Wrapper method that stops motor output to the specified port and allows it to coast. A
     * reply packet can be requested
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param reply true, if a reply status is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return {@link NXTDroidCommand#REPLY_COMPLETE} {@link NXTDroidCommand#RECEIVE_ERROR}
     *          {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int stopMotor(int port, boolean reply) {
        log("Stopping " + getMotorName(port), LOG);
        return setOutputState(port, 0, MotorMode.MOTORON, MotorRegulationMode.REGULATION_MODE_IDLE, 0, MotorRunState.MOTOR_RUN_STATE_IDLE, 0, reply);
    }

    /**
     * Wrapper method that runs the specified motor port at speed 100
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int runMotor(int port) {
        return runMotor(port, 100, false);
    }

    /**
     * Wrapper method that runs the specified motor port at the specified speed
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param power An integer, between 100 and -100 inclusive
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int runMotor(int port, int power) {
        return runMotor(port, power, false);
    }

    /**
     * Wrapper method that runs the specified motor port at the specified speed. A reply packet
     * can be requested.
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param power An integer, between 100 and -100 inclusive
     * @param reply true, if a reply status is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return {@link NXTDroidCommand#REPLY_COMPLETE} {@link NXTDroidCommand#RECEIVE_ERROR}
     *          {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int runMotor(int port, int power, boolean reply) {
        if (port == PORT_A) {
            power = (int)(scaleA * power + differenceA);
        }
        if (port == PORT_B) {
            power = (int)(scaleB * power + differenceB);
        }
        if (port == PORT_C) {
            power = (int)(scaleC * power + differenceC);
        }

        log("Running " + getMotorName(port) + " at power " + power, LOG);
        return setOutputState(port, power, MotorMode.MOTORON, MotorRegulationMode.REGULATION_MODE_MOTOR_SPEED, 0, MotorRunState.MOTOR_RUN_STATE_RUNNING, 0, reply);
    }

    /**
     * Sends a SETOUTPUTSTATE Direct Command to the NXT
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param power An integer, between 100 and -100 inclusive
     * @param mode {@link org.kealinghornets.nxtdroid.NXT.LCP.MotorMode}
     * @param reg_mode {@link org.kealinghornets.nxtdroid.NXT.LCP.MotorRegulationMode}
     * @param turn_ratio Turn ratio for sync regulated motors attempting a turn maneuver
     * @param run_state {@link org.kealinghornets.nxtdroid.NXT.LCP.MotorRunState}
     * @param tacho_limit Tachometer revolution limit
     * @param reply true, if a reply error code is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return An error code from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode} or {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int setOutputState(int port, int power, int mode, int reg_mode, int turn_ratio, int run_state, int tacho_limit, boolean reply) {
        NXTDroidCommand cmd = new NXTDroidCommand(getNXTThreadName(), DirectCommand.SETOUTPUTSTATE, reply, "setOutputState");
        nxt.port_speeds[port] = power;
        LittleEndian.setInt8(cmd.command, 2, port);
        cmd.command[3] = (byte)power;
        LittleEndian.setInt8(cmd.command, 4, mode);
        LittleEndian.setInt8(cmd.command, 5, reg_mode);
        LittleEndian.setInt8(cmd.command, 6, turn_ratio);
        LittleEndian.setInt8(cmd.command, 7, run_state);
        LittleEndian.setInt32(cmd.command, 8, tacho_limit);
        lastCommand = cmd;
        nxt.send(cmd);
        log("Sent " + DirectCommand.toString(DirectCommand.SETOUTPUTSTATE), DEBUG);
        systemSleep();
        if (reply) {
            int result = waitForReply(cmd);
            return result;
        }
        return NXTDroidCommand.NO_REPLY;
    }

    /**
     * Plays an audible tone between 500 and 14000Hz
     *
     * @param tone A frequency between 500 and 14000Hz
     * @param duration Duration of the tone in milliseconds
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int playTone(int tone, int duration) {
        return playTone(tone, duration, false);
    }

    /**
     * Plays an audible tone between 500 and 14000Hz
     *
     * @param tone A frequency between 500 and 14000Hz
     * @param duration Duration of the tone in milliseconds
     * @param reply true, if a reply error code is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return An error code from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode} or {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int playTone(int tone, int duration, boolean reply) {
        NXTDroidCommand cmd = new NXTDroidCommand(getNXTThreadName(),DirectCommand.PLAYTONE, reply, "playTone");
        LittleEndian.setInt16(cmd.command, 2, tone);
        LittleEndian.setInt16(cmd.command, 4, duration);
        lastCommand = cmd;
        nxt.send(cmd);
        log("Sent " + DirectCommand.toString(DirectCommand.PLAYTONE), LOG);
        systemSleep();
        if (reply) {
            int result = waitForReply(cmd);
            return result;
        } else {
            return NXTDroidCommand.NO_REPLY;
        }
    }

    /**
     * A wrapper method to reset a motor tachometer
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     * @see org.kealinghornets.nxtdroid.NXT.replies.MotorOutputReply#tachometer
     */
    public int resetTachometer(int port) {
        log("Resetting tachometer on " + getMotorName(port), DEBUG);
        return resetMotorPosition(port, true, false);
    }

    /**
     * A wrapper method to reset a motor rotation sensor
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     * @see org.kealinghornets.nxtdroid.NXT.replies.MotorOutputReply#rotation_count
     */
    public int resetRotationSensor(int port) {
        log("Resetting rotation sensor on " + getMotorName(port), DEBUG);
        return resetMotorPosition(port, false, false);
    }

    /**
     * Sends a RESETMOTORPOSITION Direct Command and optionally requires a reply packet
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @param relative true if just the tachometer should be reset, false if the rotation sensor should be reset
     * @param reply true, if a reply error code is required. if false, then {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     *              will be returned
     * @return An error code from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode} or {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY}
     */
    public int resetMotorPosition(int port, boolean relative, boolean reply) {
        NXTDroidCommand cmd = new NXTDroidCommand(getNXTThreadName(), DirectCommand.RESETMOTORPOSITION, reply, "resetMotorPosition");
        cmd.command[2] = (byte)port;
        cmd.command[3] = relative ? (byte)1 : (byte)0;
        lastCommand = cmd;
        nxt.send(cmd);
        log("Sent " + DirectCommand.toString(DirectCommand.RESETMOTORPOSITION), VERBOSE);
        systemSleep();
        if (reply) {
            int result = waitForReply(cmd);
            return result;
        }
        return NXTDroidCommand.NO_REPLY;
    }

    /**
     * Blocks this thread and waits for a reply packet for the specified {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand}
     *
     * @param cmd An {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand} that has been sent using
     *              {@link org.kealinghornets.nxtdroid.NXT.NXT#send(NXTDroidCommand)}
     * @return An error code from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode}
     */
    private int waitForReply(NXTDroidCommand cmd) {
        log("Awaiting reply", VERBOSE);
        while (cmd.isRunning());
        return cmd.reply[NXTDroidCommand.STATUS_BYTE_OFFSET];
    }

    /**
     * Starts this thread, sets the state of the thread to {@link NXTThread#RUNNING}
     */
    public void start() {
        if (getNXT() != null) {
            state = RUNNING;
            log("*** Thread starting ***", LOG);
            super.start();
        } else {
            log("*** No NXT specified for this thread ***");
        }

    }

    /**
     * Puts this thread to sleep for n milliseconds. This is effectively the same thing as a pause.
     *
     * @param n the number of milliseconds to pause
     */
    public void wait(int n) {
        log("Waiting for " + n + " milliseconds", LOG);
        try {
            sleep(n);
        } catch (Exception e) {
        }
    }

    /**
     * Resets this thread in the {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager} so that it
     * can be run again. It reinstantiates the thread with a call to {@link Object#clone()} but
     * uses {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager} to replace the previous
     * instance of this thread with the new one, keeping the same {@link NXTThread#id} as the old
     * one
     */
    public void reset() {
        log("*** Resetting thread ***");
        NXTThreadManager.regenerateThread(this);
        NXTThreadManager.notifyHandlers();

    }

    /**
     * Returns the name of a specified motor port
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return A String representing the specified motor output port
     */
    public String getMotorName(int port) {
        return nxt.getMotorName(port);
    }

    /**
     * Gets a String representation of the specified input port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return A String
     */
    public String getInputName(int port) {
        return nxt.getInputName(port);
    }

    /**
     * Stops all motor ports, set the thread state to {@link NXTThread#STOPPED}, logs that
     * the thread has ended, then attempts to delete the thread from {@link org.kealinghornets.nxtdroid.NXT.NXTThreadManager}.
     * It is not a requirement to call this method at the end of the
     * run() method.
     */
    public void end() {
        stopMotor(PORT_A, false);
        stopMotor(PORT_B, false);
        stopMotor(PORT_C, false);
        state = STOPPED;
        log("*** End of thread ***", LOG);
        NXTThreadManager.deleteThread(this);
        NXTThreadManager.notifyHandlers();
    }

    /**
     * Gets the output state of a port and prints a
     * {@link org.kealinghornets.nxtdroid.NXT.replies.MotorOutputReply} response to the log
     * at {@link NXTThread#LOG}
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     */
    public void printMotorState(int port) {
        MotorOutputReply reply = getOutputState(port);
        log(reply.toString() + "<br />", LOG);
    }

    /**
     * Sends a GETOUTPUTSTATE Direct Command and returns the reply packet in a
     * {@link org.kealinghornets.nxtdroid.NXT.replies.MotorOutputReply} instance
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return A {@link org.kealinghornets.nxtdroid.NXT.replies.MotorOutputReply} instance or <b>null</b> if one could not be retrieved
     */
    public MotorOutputReply getOutputState(int port) {
        NXTDroidCommand cmd = new NXTDroidCommand(getNXTThreadName(), DirectCommand.GETOUTPUTSTATE, true, "getOutputState");
        LittleEndian.setInt8(cmd.command, 2, port);
        lastCommand = cmd;
        lastCommand = cmd;
        nxt.send(cmd);
        log("Sent " + DirectCommand.toString(DirectCommand.GETOUTPUTSTATE), DEBUG);
        systemSleep();
        int result = waitForReply(cmd);
        if (result != ErrorCode.OK) {
            return null;
        }
        MotorOutputReply reply = new MotorOutputReply(cmd.reply);
        return reply;
    }

    /**
     * Creates a {@link TouchSensor} on the specified port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link TouchSensor} or <b>null</b> if
     *          initialization failed
     */
    public TouchSensor getTouchSensor(int port) {
        TouchSensor s = getNXT().getTouchSensor(port);
        if (s == null) {
            log("Could not get Touch Sensor on " + NXT.getInputName(port));
        }
        return s;
    }

    /**
     * Creates a {@link LightSensor} on the specified port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link LightSensor} or <b>null</b> if
     *          initialization failed
     */
    public LightSensor getLightSensor(int port) {
        LightSensor s = getNXT().getLightSensor(port);
        if (s == null) {
            log("Could not get Light Sensor on " + NXT.getInputName(port));
        }
        return s;
    }

    /**
     * Creates a {@link LightSensor} on the specified port with the active light
     *  setting set to true
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link LightSensor} or <b>null</b> if
     *          initialization failed
     */
    public LightSensor getActiveLightSensor(int port) {
        LightSensor s = getNXT().getActiveLightSensor(port);
        if (s == null) {
            log("Could not get Active Light Sensor on " + NXT.getInputName(port));
        }
        return s;
    }

    /**
     * Creates a {@link SoundSensor} on the specified port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link SoundSensor} or <b>null</b> if
     *          initialization failed
     */
    public SoundSensor getSoundSensordB(int port) {
        SoundSensor s = getNXT().getSoundSensordB(port);
        if (s == null) {
            log("Could not get Sound Sensor dB on " + NXT.getInputName(port));
        }
        return s;
    }

    /**
     * Creates a {@link SoundSensor} on the specified port with the read setting
     *  as dBA
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link SoundSensor} or <b>null</b> if
     *          initialization failed
     */
    public SoundSensor getSoundSensordBA(int port) {
        SoundSensor s = getNXT().getSoundSensordBA(port);
        if (s == null) {
            log("Could not get Sound Sensor dBA on " + NXT.getInputName(port));
        }
        return s;
    }

    /**
     * Creates a {@link UltrasonicSensor} on the specified port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link UltrasonicSensor} or <b>null</b> if
     *          initialization failed
     */
    public UltrasonicSensor getUltrasonicSensor(int port) {
        UltrasonicSensor s = getNXT().getUltrasonicSensor(port);
        if (s == null) {
            log("Could not get Ultrasonic Sensor on " + NXT.getInputName(port));
        }
        return s;
    }

    /**
     * Creates a {@link ColorSensor} on the specified port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link ColorSensor} or <b>null</b> if
     *          initialization failed
     */
    public ColorSensor getColorSensor(int port) {
        ColorSensor s = new ColorSensor(getNXT(), port);
        if (s.isInitialized()) {
            return s;
        } else {
            log("Could not initialize Color Sensor on " + NXT.getInputName(port));
            return null;
        }
    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.HTCompassSensor} on the specified port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return An initialized {@link org.kealinghornets.nxtdroid.NXT.HTCompassSensor} or <b>null</b> if
     *          initialization failed
     */
    public HTCompassSensor getHTCompassSensor(int port) {
        HTCompassSensor s = new HTCompassSensor(getNXT(), port);
        if (s.isInitialized()) {
            return s;
        } else {
            log("Could not initialize HT Compass Sensor on " + NXT.getInputName(port));
            return null;
        }
    }

    /**
     * Gets the NXT's battery level in millivolts
     * @return A value in millivolts or -1, if the battery level could not be read
     */
    public int getBatterylevel() {
        NXTDroidCommand cmd = new NXTDroidCommand(getNXTThreadName(), DirectCommand.GETBATTERYLEVEL, true, "getBatteryLevel");
        nxt.send(cmd);
        int result = waitForReply(cmd);
        if (result != ErrorCode.OK) { return -1; }
        int millivolts = LittleEndian.getUInt16(cmd.reply, 3);
        return millivolts;
    }

    /**
     * Sends a KeepAlive command to the NXT
     */
    public void keepAlive() {
        NXTDroidCommand cmd = new NXTDroidCommand(getNXTThreadName(), DirectCommand.KEEPALIVE, false, "keepAlive");
        nxt.send(cmd);
    }
}
