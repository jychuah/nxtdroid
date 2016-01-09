package org.kealinghornets.nxtdroid.NXT;

import org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand;
import org.kealinghornets.nxtdroid.NXT.LCP.Telegram;

import de.waldheinz.fs.fat.LittleEndian;

/**
 * Carries a command that can be sent by an {@link org.kealinghornets.nxtdroid.NXT.NXTThread}
 * to a physical NXT via an instance of {@link org.kealinghornets.nxtdroid.NXT.NXT} and
 * receives reply bytes if requested. The object is unaware of the semantics or meaning of the command.
 * It only maintains the state of the command and its reply.
 */
public class NXTDroidCommand {

    /**
     * Command is being initialized
     */
    public static final int INITIALIZING = 0;

    /**
     * Command is in a send queue, awaiting transmission
     */
    public static final int QUEUED = 1;

    /**
     * Command has been transmitted via Bluetooth and is awaiting reply
     */
    public static final int RUNNING = 2;

    /**
     * Command has been transmitted and received a complete reply via Bluetooth
     */
    public static final int REPLY_COMPLETE = 3;

    /**
     * Command has been transmitted via Bluetooth and does not require a reply
     */
    public static final int NO_REPLY = -1;

    /**
     * Command was not successfully transmitted via Bluetooth
     */
    public static final int SEND_ERROR = 4;

    /**
     * Command was successfully transmitted, but the reply was not successfully received
     */
    public static final int RECEIVE_ERROR = 5;

    /**
     * Command reply was garbled
     */
    public static final int REPLY_UNKNOWN = -2;

    /**
     * Maximum command byte array length
     */
    public static final int MAX_LENGTH = 64;

    /**
     * Offset of telegram type byte, according to the NXT Direct Command specification
     * @see org.kealinghornets.nxtdroid.NXT.LCP.Telegram
     */
    public static final int TELEGRAM_TYPE_OFFSET = 0;

    /**
     * Offset of the Direct Rommand OpCode, according to the NXT Direct Command specification
     * @see org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand
     */
    public static final int COMMAND_OFFSET = 1;

    /**
     * Offset of the reply status byte, according to the NXT Direct Command specification
     * @see org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode
     */
    public static final int STATUS_BYTE_OFFSET = 2;

    String description = "";
    private int state = INITIALIZING;
    long queueTimeStamp = 0;
    long sendTimeStamp = 0;
    long replyTimeStamp = 0;
    boolean requiresReply = false;
    private Object lock = new Object();
    private int replyPosition = 0;
    String originator;

    /**
     * Command bytes to be transmitted. The default length is {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#MAX_LENGTH}
     */
    public byte[] command = new byte[MAX_LENGTH];

    /**
     * Command packet length, set automatically by {@link NXTDroidCommand#NXTDroidCommand(NXTThread, byte, boolean, String)}. The
     * length can be overwritten before being sent, such as in the case of {@link org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand#LSWRITE}
     * where the packet length is variable.
     */
    public int cmdLength = 0;

    /**
     * Command reply bytes. After the command is sent via an {@link org.kealinghornets.nxtdroid.NXT.NXT}, this array will be
     * filled with a reply packet if the comamnd requires a reply.
     */
    public byte[] reply = new byte[MAX_LENGTH];

    /**
     * A test tone Direct Command packet (including length header)
     */
    public static byte[] testTone = { (byte)0x06, (byte)0x00, (byte)0x80, (byte)0x03, (byte)0x0b, (byte)0x02, (byte)0xf4, (byte)0x01 };

    /**
     * This constructor automatically sets the default packet length and telegram type.
     * @param originator The originating {@link org.kealinghornets.nxtdroid.NXT.NXTThread}
     * @param cmd The Direct Command OpCode to send
     * @param requireReply true, if the command should block the {@link org.kealinghornets.nxtdroid.NXT.NXT} send queue until
     *                     a reply packet is received
     * @param comment A comment to add for thread events
     * @see org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand
     */
    public NXTDroidCommand(String originator, byte cmd, boolean requireReply, String comment) {
        this.originator = originator;
        this.description = comment;
        for (int i = 0; i < MAX_LENGTH; command[i] = 0, reply[i++] = 0);
        LittleEndian.setInt8(command, COMMAND_OFFSET, cmd);
        if (requireReply) {
            requireReply();
        } else {
            doNotRequireReply();
        }
        cmdLength = DirectCommand.SendLength[cmd];
    }

    void setState(int n) {
        synchronized(lock) {
            state = n;
        }
    }

    /**
     * Gets the current state of the command, set internally by the {@link org.kealinghornets.nxtdroid.NXT.NXT}
     * that is sending it.
     *
     * @return A constant representing the state of the command
     */
    public int getState() {
        synchronized(lock) {
            return state;
        }
    }

    public void requireReply() {
        requiresReply = true;
        if (DirectCommand.SystemCommand[command[COMMAND_OFFSET]]) {
            command[TELEGRAM_TYPE_OFFSET] = Telegram.SC_REPLY_REQUIRED;
        } else {
            command[TELEGRAM_TYPE_OFFSET] = Telegram.DC_REPLY_REQUIRED;
        }
    }

    public void doNotRequireReply() {
        requiresReply = false;
        if (DirectCommand.SystemCommand[command[COMMAND_OFFSET]]) {
            command[TELEGRAM_TYPE_OFFSET] = Telegram.SC_NO_REPLY;
        } else {
            command[TELEGRAM_TYPE_OFFSET] = Telegram.DC_NO_REPLY;
        }
    }

    /**
     * Returns true if the current state is either {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#QUEUED}
     * or {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand#RUNNING}
     *
     * @return
     */
    public boolean isRunning() {
        int state = getState();
        return state == QUEUED || state == RUNNING;
    }

    /**
     * Sets the Telegram type byte of the command packet
     *
     * @param type A Telegram byte code
     * @see org.kealinghornets.nxtdroid.NXT.LCP.Telegram
     */
    public void setTelegramType(int type) {
        LittleEndian.setInt8(command, TELEGRAM_TYPE_OFFSET, type);
    }

/*
    void addToReply(byte b) {
        reply[replyPosition] = b;
        replyPosition++;
    }
*/
}
