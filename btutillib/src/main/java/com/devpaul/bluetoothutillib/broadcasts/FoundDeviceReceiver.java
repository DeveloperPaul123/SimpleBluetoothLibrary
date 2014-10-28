package com.devpaul.bluetoothutillib.broadcasts;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Paul Tsouchlos
 */
public class FoundDeviceReceiver extends BroadcastReceiver {
    private FoundDeviceReceiverCallBack mCallBack;

    /**
     * Constructor for a {@code FoundDeviceReceiver}
     * @param callBack callback to notify calling activity when devices are found.
     */
    public FoundDeviceReceiver(FoundDeviceReceiverCallBack callBack) {
        mCallBack = callBack;
    }

    /**
     * Registers a new FoundDeviceReceiver
     * @param context the context
     * @param callBack callback from the context.
     * @return return a new FoundDeviceReceiver
     */
    public static FoundDeviceReceiver register(Context context, FoundDeviceReceiverCallBack callBack) {
        FoundDeviceReceiver fdr = new FoundDeviceReceiver(callBack);
        context.registerReceiver(fdr, getIntentFilter());
        return fdr;
    }

    /**
     * Helper method to get the intent filter.
     * @return Intent filter for BluetoothDevice.ACTION_FOUND
     */
    private static IntentFilter getIntentFilter() {
        return new IntentFilter(BluetoothDevice.ACTION_FOUND);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // Add the name and address to an array adapter to show in a ListView
            if(mCallBack != null) {
                mCallBack.onDeviceFound(device);
            }
        }
    }

    /**
     * Callback for this class. Only one method.
     */
    public static interface FoundDeviceReceiverCallBack {
        public void onDeviceFound(BluetoothDevice device);
    }

    /**
     * Helper method to unregister a receiver.
     * @param context the context
     * @param receiver the receiver to unregister.
     */
    public static void safeUnregister(Context context, FoundDeviceReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch(IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
