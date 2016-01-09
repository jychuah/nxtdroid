package org.kealinghornets.nxtdroid.NXT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.kealinghornets.nxtdroid.NXT.LCP.DirectCommand;
import org.kealinghornets.nxtdroid.NXT.LCP.ErrorCode;

import de.waldheinz.fs.fat.LittleEndian;

/**
 * Represents an individual NXT, with managed thread safe send and receive queues. Multiple instances
 * of {@link org.kealinghornets.nxtdroid.NXT.NXTThread} may generate
 * {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand} objects and send them to a single
 * NXT using an instance of this class. Each {@link org.kealinghornets.nxtdroid.NXT.NXTDroidCommand}
 * that requires a reply will receive one in the order in which they were sent.
 */
public class NXT {
    /**
     * Motor output and encoder Port A
     */
    public static final byte PORT_A = (byte)0x00;

    /**
     * Motor output and encoder Port B
     */
    public static final byte PORT_B = (byte)0x01;

    /**
     * Motor output and encoder Port C
     */
    public static final byte PORT_C = (byte)0x02;

    /**
     * Sensor input Port 1
     */
    public static final byte PORT_1 = (byte)0x00;

    /**
     * Sensor input Port 2
     */
    public static final byte PORT_2 = (byte)0x01;

    /**
     * Sensor input Port 3
     */
    public static final byte PORT_3 = (byte)0x02;

    /**
     * Sensor input Port 4
     */
    public static final byte PORT_4 = (byte)0x03;

    private Queue<NXTDroidCommand> sendQueue = new LinkedBlockingQueue<NXTDroidCommand>();
    private NXTDroidCommand lastCommand = null;
    InputStream replyStream = null;
    OutputStream sendStream = null;
    private int sendState = OK_TO_SEND;
	public String deviceName = "";
	public String deviceAddress = "";
    private ArrayList<Handler> threadEventHandlers = new ArrayList<Handler>();
    private boolean connected = false;

    public String threadEventLog = "";
    private SendThread sender = new SendThread(this);
    private ReceiveThread receiver = new ReceiveThread(this);

    private static int sendDelay = 10;
	private static final int TIMEOUT_PERIOD = 100;
    private static final int REPLY_RECEIVED = 4;
	private static final int COMMAND_ERROR = 3;
	private static final int REQUEST_TIMEOUT = 2;
	private static final int REQUEST_REPLY = 1;
	private static final int OK_TO_SEND = 0;

    int[] port_speeds = new int[4];

    Sensor[] sensors = new Sensor[4];

	private Object lock = new Object();

    private NXTConnectionManager manager;

    NXT(NXTConnectionManager manager) {
        this.manager = manager;
    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.TouchSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.TouchSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public TouchSensor getTouchSensor(int port) {
        if (sensors[port] != null && sensors[port] instanceof TouchSensor) { return (TouchSensor)sensors[port]; }
        return (TouchSensor)checkSensorInit(new TouchSensor(this, port), "Touch Sensor");

    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.LightSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port. The light sensor will be
     * in inactive mode.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.LightSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public LightSensor getLightSensor(int port) {
        if (sensors[port] != null && sensors[port] instanceof LightSensor && !((LightSensor)sensors[port]).active) { return (LightSensor)sensors[port]; }
        return (LightSensor)checkSensorInit(new LightSensor(this, port), "Light Sensor");

    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.LightSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port. The light sensor will
     * be in active mode.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.LightSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public LightSensor getActiveLightSensor(int port) {
        if (sensors[port] != null && sensors[port] instanceof LightSensor && ((LightSensor)sensors[port]).active) { return (LightSensor)sensors[port]; }
        return (LightSensor)checkSensorInit(new LightSensor(this, port, true), "Active Light Sensor");

    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.SoundSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port. The sound sensor will be in
     * dB mode.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.SoundSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public SoundSensor getSoundSensordB(int port) {
        if (sensors[port] != null && sensors[port] instanceof SoundSensor && !((SoundSensor)sensors[port]).dBA) { return (SoundSensor)sensors[port]; }
        return (SoundSensor)checkSensorInit(new SoundSensor(this, port), "Sound Sensor dB");

    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.SoundSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port. The sound sensor will be in
     * dBA mode.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.SoundSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public SoundSensor getSoundSensordBA(int port) {
        if (sensors[port] != null && sensors[port] instanceof SoundSensor && ((SoundSensor)sensors[port]).dBA) { return (SoundSensor)sensors[port]; }
        return (SoundSensor)checkSensorInit(new SoundSensor(this, port, true), "Sound Sensor dBA");

    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.UltrasonicSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.UltrasonicSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public UltrasonicSensor getUltrasonicSensor(int port) {
        if (sensors[port] != null && sensors[port] instanceof UltrasonicSensor) { return (UltrasonicSensor)sensors[port]; }
        return (UltrasonicSensor)checkSensorInit(new UltrasonicSensor(this, port), "Ultrasonic Sensor");

    }

    /**
     * Creates a {@link org.kealinghornets.nxtdroid.NXT.ColorSensor} instance and ties it to the specified port,
     * or returns a pre-existing one if a matching sensor already exists at that port.
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.ColorSensor} or <b>null</b> if the sensor
     *    failed to initialize
     * @see org.kealinghornets.nxtdroid.NXT.Sensor#isInitialized()
     */
    public ColorSensor getColorSensor(int port) {
        if (sensors[port] != null && sensors[port] instanceof ColorSensor) { return (ColorSensor)sensors[port]; }
        return (ColorSensor)checkSensorInit(new ColorSensor(this, port), "Color Sensor");
    }

    Sensor checkSensorInit(Sensor s, String type) {
        if (s.isInitialized()) {
            sensors[s.port] = s;
            return s;
        } else {
            addThreadEvent("Could not initialize " + type + " on " + NXT.getInputName(s.port));
            sensors[s.port] = null;
            return null;
        }
    }

    /**
     * Registers a given {@link Sensor} at the specified port
     *
     * @param sensor An instance of {@link Sensor}
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     */
    public void setSensor(Sensor sensor, int port) {
        sensors[port] = sensor;
    }

    /**
     * Gets a registered {@link Sensor} from the specified port
     *
     * @param port {@link NXT#PORT_1} {@link NXT#PORT_2} {@link NXT#PORT_3} {@link NXT#PORT_4}
     * @return An instance of {@link Sensor}
     */
    public Sensor getSensor(int port) {
        return sensors[port];
    }

    /**
     * Returns whether or not this instance is connected via Bluetooth to an NXT
     *
     * @return true, if connected.
     */
    public boolean isConnected() {
        synchronized (lock) {
            return connected;
        }
    }

	void closeConnection() {
        synchronized(lock) {
            try {
                Log.d("NXT", "Closing connection to NXT");
                sender.running = false;
                receiver.running = false;
                connected = false;
            } catch (Exception e) {
            }
        }
	}

    void initConnection() {
        sender.start();
        receiver.start();
        sendState = OK_TO_SEND;
    }

    /**
     * Registers a {@link android.os.Handler} to receive empty {@link android.os.Message}
     * objects whenever a send or receive thread event occurs.
     *
     * @param h The {@link android.os.Handler} to register
     */
    public void registerThreadEventHandler(Handler h) {
        threadEventHandlers.add(h);
    }

    /**
     * Force notification of all {@link android.os.Handler} instances registered with this instance.
     *
     * @see NXT#notifyThreadEventHandlers()
     */
    public void notifyThreadEventHandlers() {
        Message m = new Message();
        for (Handler h : threadEventHandlers) {
            h.sendMessage(m);
        }
    }

    /**
     * Registers an event in this NXT's thread log
     *
     * @param event A string representing the event to add
     */
    public void addThreadEvent(String event) {
        threadEventLog += event + "<br />";
        notifyThreadEventHandlers();
    }

    /**
     * Gets the state of the send thread.
     *
     * @return The state of the thread - {@link NXTThread#RUNNING} {@link NXTThread#READY} {@link NXTThread#STOPPED}
     */
    public int getSendThreadState() {
        if (sender.getState() == Thread.State.NEW) {
            return NXTThread.READY;
        }
        if (sender.getState() == Thread.State.TIMED_WAITING || sender.getState() == Thread.State.WAITING ||
                sender.getState() == Thread.State.BLOCKED || sender.getState() == Thread.State.RUNNABLE) {
            return NXTThread.RUNNING;
        }
        if (sender.isAlive() == false) {
            return NXTThread.STOPPED;
        }
        return -1;
    }

	private void setSendState(int newState) {
		synchronized(lock) {
			sendState = newState;
		}
	}

    /**
     * Gets the state of the send thread
     *
     * @return The state of the thread - {@link org.kealinghornets.nxtdroid.NXT.NXT#OK_TO_SEND} {@link NXT#REQUEST_REPLY}
     */
	public int getSendState() {
		synchronized(lock) {
			return sendState;
		}
	}

    /**
     * Queues an NXTDroidCommand instance to be sent to the NXT
     *
     * @param cmd The command object to be sent.
     * @see org.kealinghornets.nxtdroid.NXT.NXTDroidCommand
     */
    public void send(NXTDroidCommand cmd) {
        synchronized(lock) {
            cmd.setState(NXTDroidCommand.QUEUED);
            sendQueue.add(cmd);
        }
    }

    /**
     * Returns the name of a specified motor port
     *
     * @param port {@link NXTThread#PORT_A} {@link NXTThread#PORT_B} {@link NXTThread#PORT_C}
     * @return A String representing the specified motor output port
     */
    public static String getMotorName(int port) {
        switch (port) {
            case PORT_A : return "Motor A";
            case PORT_B : return "Motor B";
            case PORT_C : return "Motor C";
            default : return "Motor UNKNOWN";
        }
    }

    /**
     * Gets a String representation of the specified input port
     * @param port {@link NXTThread#PORT_1} {@link NXTThread#PORT_2} {@link NXTThread#PORT_3} {@link NXTThread#PORT_4}
     * @return A String
     */
    public static String getInputName(int port) {
        switch(port) {
            case PORT_1 : return("Port 1");
            case PORT_2 : return ("Port 2");
            case PORT_3 : return ("Port 3");
            case PORT_4 : return ("Port 4");
            default : return "Port UNKNOWN";
        }
    }

    /**
     * Gets the delay between message queue sends. The default value is 70
     *
     * @return A delay in milliseconds
     */
    public static int getSendDelay() {
        return sendDelay;
    }

    /**
     * Sets the delay between message queue sends. Lower times may result in Bluetooth
     * communication errors. Messages sent that do not require a reply result in the next message
     * being sent in half the delay. The default value is 70.
     *
     * @param sendDelay A delay in milliseconds
     */
    public static void setSendDelay(int sendDelay) {
        NXT.sendDelay = sendDelay;
    }

    String packetToString(byte[] buffer, int msgLength) {
        String s = "Packet of length " + msgLength + ":";
        for (int i = 0; i < msgLength; i++) {
            s = s + " " + (byte)buffer[i];
        }
        return s;
    }

	private class ReceiveThread extends Thread {
		NXT mNXT = null;
        boolean running = true;
		public ReceiveThread(NXT mNXT) {
			this.mNXT = mNXT;
		}
		
		public void run() {
            addThreadEvent("Receiver: running");
			while(running) {
                try {
                    sleep(25);
                    if (lastCommand != null) {
                        if (getSendState() == REQUEST_REPLY) {
                            try {
                                if (lastCommand.command[NXTDroidCommand.COMMAND_OFFSET] == DirectCommand.LSWRITE) {
                                    addThreadEvent("Attempting LSWRITE");
                                }
                                byte[] word = new byte[2];
                                word[0] = (byte)replyStream.read();
                                word[1] = (byte)replyStream.read();
                                int msgLength = LittleEndian.getUInt16(word, 0);
                                replyStream.read(lastCommand.reply, 0, msgLength);
                                addThreadEvent("Receiver: " + ErrorCode.toString(lastCommand.reply[NXTDroidCommand.STATUS_BYTE_OFFSET]));
                                lastCommand.replyTimeStamp = System.currentTimeMillis();
                                lastCommand.setState(NXTDroidCommand.REPLY_COMPLETE);
                                setSendState(OK_TO_SEND);
                            } catch (IOException e) {
                                lastCommand.replyTimeStamp = System.currentTimeMillis();
                                lastCommand.setState(NXTDroidCommand.RECEIVE_ERROR);
                                Bundle b = new Bundle();
                                b.putString(NXTConnectionManager.LCP_DISCONNECT_DEVICEADDRESS, mNXT.deviceAddress);
                                Intent intent = new Intent();
                                intent.setAction(NXTConnectionManager.LCP_DISCONNECT);
                                intent.putExtras(b);
                                manager.context.sendBroadcast(intent);
                            }
                        }
                    }
                } catch (Exception e) {
                }
			}
            try {
                replyStream.close();
            } catch (IOException e) {

            }
		}
	}
	
	private class SendThread extends Thread {
		NXT mNXT = null;
        boolean running = true;
        boolean noReply = false;

		public SendThread(NXT mNXT) {
			this.mNXT = mNXT;
		}
		public void run() {
            addThreadEvent("Sender: running");

			while(running) {
                try {
                    sleep(noReply ? sendDelay / 2 : sendDelay);
//                    Log.d("nxtwake", "wake");
                    if (getSendState() == OK_TO_SEND) {
                        if (!sendQueue.isEmpty()) {
                            lastCommand = sendQueue.remove();
  //                          Log.d("nxtsendqueue", "send queue has item: " + lastCommand.description);
                            if (lastCommand != null) {
                                lastCommand.sendTimeStamp = System.currentTimeMillis();
                                if (lastCommand.requiresReply) {
                                    lastCommand.setState(NXTDroidCommand.RUNNING);
                                    setSendState(REQUEST_REPLY);
                                    noReply = false;
                                } else {
                                    lastCommand.setState(NXTDroidCommand.NO_REPLY);
                                    noReply = true;
                                }
                                try {
                                    sendStream.write(lastCommand.cmdLength);
                                    sendStream.write((byte)0);
                                    sendStream.write(lastCommand.command, 0, lastCommand.cmdLength);
                                    sendStream.flush();
                                } catch (IOException e) {
                                    lastCommand.setState(NXTDroidCommand.SEND_ERROR);
                                }
                                addThreadEvent("Sender: " + DirectCommand.toString(lastCommand.command[NXTDroidCommand.COMMAND_OFFSET]) +
                                        (lastCommand.originator != null ? (" from " + lastCommand.originator) : "")  +
                                        ((lastCommand.description != null && lastCommand.description.length() > 0) ? (" (" + lastCommand.description + ")") : "") );
                            }
                        } else {
    //                        Log.d("nxtsendqueue", "send queue empty");
                        }
                    } else {
      //                  Log.d("nxtsendstate", "not ok to send");
                    }
                } catch (Exception e) {
                }
			}
            try {
                sendStream.close();
            } catch (IOException e) {

            }
		}
	}
}
