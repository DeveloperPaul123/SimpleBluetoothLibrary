package com.devpaul.bluetoothutillib.broadcasts;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Paul Tsouchlos
 */
public class BluetoothStateReceiver extends BroadcastReceiver{

    private Callback mCallback;

    public BluetoothStateReceiver(Callback callback) {
        this.mCallback = callback;
    }

    /**
     * Register this receiver with a series of intent filters.
     * @param context the context that will register the receiver.
     * @param callback the callback to notify when there are changes.
     * @return an instance of the {@code BluetoothStateReceiver}
     */
    public static BluetoothStateReceiver register(Context context, Callback callback) {

        //create the intent filters.
        IntentFilter connected = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disconnectRequest = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter disconnected = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter discoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter discoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        BluetoothStateReceiver bluetoothStateReceiver = new BluetoothStateReceiver(callback);

        //register for each filter.
        context.registerReceiver(bluetoothStateReceiver, connected);
        context.registerReceiver(bluetoothStateReceiver, disconnectRequest);
        context.registerReceiver(bluetoothStateReceiver, disconnected);
        context.registerReceiver(bluetoothStateReceiver, discoveryFinished);
        context.registerReceiver(bluetoothStateReceiver, discoveryStarted);
        return bluetoothStateReceiver;
    }

    /**
     * Safe method to unregister the receiver in case of errors. Still unregisters the receiver for
     * all filters it has been registered for.
     * @param context the context that had registered the receiver
     * @param receiver the receiver that was registered.
     */
    public static void safeUnregister(Context context, BluetoothStateReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch(IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
           //Device is now connected
            mCallback.onDeviceConnected(device);
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //Done searching
            mCallback.onDiscoveryFinished();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //Device has disconnected
            mCallback.onDeviceDisconnected(device);
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            //discovery started.
            mCallback.onDiscoveryStarted();
        }
    }


    /**
     * Callback interface for this class.
     */
    public interface Callback {
        public void onDeviceConnected(BluetoothDevice device);
        public void onDeviceDisconnected(BluetoothDevice device);
        public void onDiscoveryFinished();
        public void onDiscoveryStarted();
    }
}
