package org.kealinghornets.nxtdroid.app.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothConnectActivityReceiver extends BroadcastReceiver {
    private BluetoothConnector connector;

    public BluetoothConnectActivityReceiver(BluetoothConnector connector) {
        this.connector = connector;
    }

    @Override 
    public void onReceive(Context context, Intent intent)  {
        if (BluetoothDevicePicker.ACTION_DEVICE_SELECTED.equals(intent.getAction())) {
            context.unregisterReceiver(this);            
            BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = device.getName();
            String address = device.getAddress();
            connector.connectToService(name, address);
        }
    }
}