package org.kealinghornets.nxtdroid.NXT;

import android.os.Bundle;

import org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand;
import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;

/**
 * An abstract sensor class. Any child, upon instantiation, will register itself with an {@link org.kealinghornets.nxtdroid.NXT.NXT}
 * on the specified port. To create a subclass, {@link Sensor#doSensorPoll()} must be implemented. This class also uses
 * a sensor refresh threshold which prevents a Bluetooth sensor poll to the NXT if one was recently made. The default
 * refresh threshold is 100 milliseconds.
 *
 * @see Sensor#getValue()
 */
abstract class Sensor {
    NXT nxt;

    long lastUpdateTime = 0;
    int sensorType;
    int sensorMode;
    int port;
    Bundle lastValue = null;
    boolean initialized = false;

    int refreshThreshold = 100;

    /**
     * Constructor that calls {@link Sensor#setInputMode(byte, byte)}, blocks for a reply, then logs the resulting error
     * code on the thread log of the {@link org.kealinghornets.nxtdroid.NXT.NXT} parameter. In addition, this
     * instance is saved in the internal sensor record of the {@link org.kealinghornets.nxtdroid.NXT.NXT}
     *
     * @param nxt An {@link org.kealinghornets.nxtdroid.NXT.NXT} to attach the sensor to
     * @param port {@link org.kealinghornets.nxtdroid.NXT.NXT#PORT_1} {@link org.kealinghornets.nxtdroid.NXT.NXT#PORT_2}
     *             {@link org.kealinghornets.nxtdroid.NXT.NXT#PORT_3} {@link org.kealinghornets.nxtdroid.NXT.NXT#PORT_4}
     * @param sensorType A sensor type from {@link org.kealinghornets.nxtdroid.NXT.LCP.SensorType}
     * @param sensorMode A sensor mode from {@link org.kealinghornets.nxtdroid.NXT.LCP.SensorMode}
     */
    Sensor(NXT nxt, int port, byte sensorType, byte sensorMode) {
        this.nxt = nxt;
        this.port = port;
        byte result = setInputMode(sensorType, sensorMode);
        initialized = result == ErrorCode.OK;
        nxt.addThreadEvent("Sensor SETINPUTMODE on " + NXT.getInputName(port) + " returned " + ErrorCode.toString(result));
    }

    /**
     * Get the {@link org.kealinghornets.nxtdroid.NXT.NXT} this Sensor is attached to
     *
     * @return An {@link org.kealinghornets.nxtdroid.NXT.NXT}
     * @see Sensor#Sensor(NXT, int, byte, byte)
     */
    public NXT getNxt() {
        return nxt;
    }

    /**
     * Get the refresh threshold value in milliseconds
     *
     * @return The refresh threshold
     * @see Sensor#getValue()
     */
    public int getRefreshThreshold() {
        return refreshThreshold;
    }

    /**
     * Sets the refresh threshold value in milliseconds
     *
     * @param refreshThreshold
     * @see Sensor#getValue()
     */
    public void setRefreshThreshold(int refreshThreshold) {
        this.refreshThreshold = refreshThreshold;
    }

    /**
     * Returns the last time the remote NXT sensor was polled in system milliseconds
     *
     * @return A value from {@link System#currentTimeMillis()}
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Gets the last sensor value polled from the NXT
     *
     * @return A {@link android.os.Bundle} with sensor values
     */
    public Bundle getLastValue() {
        return lastValue;
    }

    /**
     * Determines whether or not the last sensor value is current, within the refresh threshold
     * @return
     */
    public boolean sensorNeedsRefresh() {
        return System.currentTimeMillis() - lastUpdateTime > getRefreshThreshold();
    }

    /**
     * Checks to see if the sensor requires a refresh with {@link Sensor#sensorNeedsRefresh()}. If it
     * does not, {@link Sensor#getLastValue()} is returned. Otherwise,
     * {@link Sensor#doSensorPoll()} is called to poll the NXT. The result is saved as
     * the last value, the last update time is updated and the value is returned.
     *
     * @return A {@link android.os.Bundle} containing sensor information
     */
    synchronized Bundle getValue() {
        if (!sensorNeedsRefresh()) {
            return getLastValue();
        } else {
            lastValue = doSensorPoll();
            lastUpdateTime = System.currentTimeMillis();
            return lastValue;
        }
    }

    /**
     * Blocks execution while the given command is running, then returns its status byte
     *
     * @return A status byte from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode}
     */
    byte waitForReply(NXTDroidCommand cmd) {
        while (cmd.isRunning());
        return cmd.reply[NXTDroidCommand.STATUS_BYTE_OFFSET];
    }

    /**
     * Sets a sensor port to the specified type and mode. If the port already matches the specified types,
     * no command is actually sent and {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#NO_REPLY} is returned
     * @param sensorType A sensor type from {@link org.kealinghornets.nxtdroid.NXT.LCP.SensorType}
     * @param sensorMode A sensor mode from {@link org.kealinghornets.nxtdroid.NXT.LCP.SensorMode}
     * @return An error code from {@link org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode}
     */
    byte setInputMode(byte sensorType, byte sensorMode) {
        NXTDroidCommand cmd = new NXTDroidCommand("Sensor Init on " + nxt.getInputName(port), DirectCommand.SETINPUTMODE, true, null);
        cmd.command[2] = (byte)port;
        cmd.command[3] = sensorType;
        cmd.command[4] = sensorMode;
        nxt.send(cmd);
        byte result = waitForReply(cmd);
        return result;
    }

    /**
     * Override with code to poll the remote NXT for a sensor value and return a {@link android.os.Bundle} with
     * sensor information.
     *
     * @return A {@link android.os.Bundle}
     */
    abstract Bundle doSensorPoll();

    /**
     * Determine if sensor initialized successfully
     *
     * @return true, if the sensor successfully initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}
