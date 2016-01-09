package org.kealinghornets.nxtdroid.NXT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

/**
 * Creates and manages instances of {@link org.kealinghornets.nxtdroid.NXT.NXT}, sets up
 * Bluetooth communication streams for them and manages their connection state. It also enforces
 * that any single physical NXT only has one instance of {@link org.kealinghornets.nxtdroid.NXT.NXT}
 * associated with it. If a duplicate request for one physical NXT is made, the same instance of
 * {@link org.kealinghornets.nxtdroid.NXT.NXT} is returned. If a physical NXT is disconnected,
 * the associated {@link org.kealinghornets.nxtdroid.NXT.NXT} instance is shut down.
 *
 * <b>NXT units must already be paired in the host operating system's Bluetooth connection manager.</b>
 */
public class NXTConnectionManager {
    private static HashMap<String, NXT> nxtRegistry = new HashMap<String, NXT>();
    private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    public static final int CONNECT_SUCCEEDED = 0;
    public static final int CONNECT_FAILED = 1;
    public static final int CONNECT_ALREADY_CONNECTED = 2;
    private BluetoothDevice btDevice;
    private BluetoothSocket btSocket;
    public static final String LCP_DISCONNECT = "org.kealinghornets.nxtdroid.thread.NXTConnectionManager.LCP_DISCONNECT";
    public static final String LCP_DISCONNECT_DEVICEADDRESS = "org.kealinghornets.nxtdroid.thread.NXTConnectionManager.LCP_DISCONNECT_DEVICEADDRESS";
    public static Context context = null;

    static NXT defaultNXT = null;

    /**
     * Creates a new NXTConnectionManager instance, tied to an application Context
     *
     * @param context The {@link android.content.Context} with which the NXTConnectionManager
     *                will send and receive {@link android.content.Intent} messages.
     */
    public NXTConnectionManager(Context context) {
        this.context = context;
    }

    /**
     * Gets an array of connected NXTs
     *
     * @return An array of unique {@link org.kealinghornets.nxtdroid.NXT.NXT} instances, or null if no
     *          NXTs are connected
     */
    public static NXT[] getConnectedNXTs() {
        if (nxtRegistry.keySet().size() == 0) {
            return null;
        }
        int i = 0;
        NXT[] result = new NXT[nxtRegistry.keySet().size()];
        for (String address : nxtRegistry.keySet()) {
            result[i++] = nxtRegistry.get(address);
        }
        return result;
    }

    /**
     * Tries to get the first valid connected {@link org.kealinghornets.nxtdroid.NXT.NXT} that
     * was connected
     *
     * @return An instance of {@link org.kealinghornets.nxtdroid.NXT.NXT} or <b>null</b>
     */
    public static NXT getDefaultNXT() {
        if (defaultNXT == null) {
            NXT[] nxtList = getConnectedNXTs();
            if (nxtList != null && nxtList.length > 0 && nxtList[0] != null) {
                defaultNXT = nxtList[0];
            }
        }
        return defaultNXT;
    }

    /**
     * Sets the default {@link org.kealinghornets.nxtdroid.NXT.NXT} returned by
     * {@link NXTConnectionManager#getDefaultNXT()}
     *
     * @param nxt An instance of {@link org.kealinghornets.nxtdroid.NXT.NXT}
     */
    public static void setDefaultNXT(NXT nxt) {
        defaultNXT = nxt;
    }

    private void registerDisconnectReceiver() {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                Bundle b = intent.getExtras();
                String address = b.getString(LCP_DISCONNECT_DEVICEADDRESS);
                NXT nxt = nxtRegistry.get(address);
                nxt.closeConnection();
                if (defaultNXT == nxt) {
                    defaultNXT = null;
                }
                nxtRegistry.remove(address);
                NXTThreadManager.notifyNXTDisconnect(nxt);
                registerDisconnectReceiver();
            }
        }, new IntentFilter(LCP_DISCONNECT));
    }
    private void registerNXT(String deviceAddress, NXT nxt) {
        nxtRegistry.put(deviceAddress, nxt);
    }

    /**
     * Returns an already connected {@link org.kealinghornets.nxtdroid.NXT.NXT} given a
     * Bluetooth hardware address
     *
     * @param deviceAddress The MAC address of a physical NXT
     * @return The {@link org.kealinghornets.nxtdroid.NXT.NXT} instance associated with the
     *          MAC address, or <b>null</b> if no valid {@link org.kealinghornets.nxtdroid.NXT.NXT}
     *          instance exists for that address.
     */
    public static NXT getNXTByAddress(String deviceAddress) {
        return nxtRegistry.get(deviceAddress);
    }

    /**
     * Disconnects an {@link org.kealinghornets.nxtdroid.NXT.NXT} instance. The physical
     * NXT can be connected again, with a new instance of {@link org.kealinghornets.nxtdroid.NXT.NXT}
     * generated for the new connection.
     *
     * @param deviceAddress The MAC address of a physical NXT
     * @return <b>true</b> if the device was found and disconnected
     */
    public static boolean disconnect(String deviceAddress) {
        NXT nxt = getNXTByAddress(deviceAddress);
        if (nxt != null) {
            nxt.closeConnection();
            nxtRegistry.remove(deviceAddress);
            return true;
        }
        return false;
    }

    /**
     * Gets the Bluetooth friendly name of a given device MAC address
     *
     * @param deviceAddress The MAC address of a physical NXT
     * @return A String representing the Bluetooth friendly name
     */
    public static String getNXTNameFromAddress(String deviceAddress) {
        NXT nxt = getNXTByAddress(deviceAddress);
        if (nxt == null) {
            return "";
        } else {
            return nxt.deviceName;
        }
    }

    /**
     * Establishes a connection to an NXT device and either creates a new instance of
     * {@link org.kealinghornets.nxtdroid.NXT.NXT} if no connection currently exists or returns
     * the pre-existing instance of {@link org.kealinghornets.nxtdroid.NXT.NXT} that represents
     * the connection. <b>The device must already be paired in the host operating system.</b>
     *
     * @param deviceAddress The MAC address of a physical NXT
     * @return An {@link org.kealinghornets.nxtdroid.NXT.NXT} instance, or null if no connection
     *          could be established.
     */
    public NXT connect(String deviceAddress) {
        NXT result = nxtRegistry.get(deviceAddress);
        if (result != null) { return result; }
        try {
            btDevice = btAdapter.getRemoteDevice(deviceAddress);
            btSocket = btDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
            btSocket.connect();
            OutputStream sendStream = btSocket.getOutputStream();
            InputStream replyStream = btSocket.getInputStream();
            sendStream.write(NXTDroidCommand.testTone);
            sendStream.flush();
            result = new NXT(this);
            result.sendStream = sendStream;
            result.replyStream = replyStream;
            result.deviceAddress = deviceAddress;
            result.deviceName = btDevice.getName();
            result.initConnection();
            registerNXT(deviceAddress, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
