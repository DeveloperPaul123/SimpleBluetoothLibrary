package com.devpaul.bluetoothutillib.broadcasts;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Paul Tsouchlos
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {

    private BroadcastCallback mCallaback;

    /**
     * BluetoothBroadcastReceiver listens for if bluetooth is enabled.
     * @param callback the callback to notify when bluetooth is enabled.
     */
    public BluetoothBroadcastReceiver(BroadcastCallback callback) {
        this.mCallaback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if(action == BluetoothAdapter.ACTION_STATE_CHANGED) {

            int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.ERROR);
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if(previousState == BluetoothAdapter.STATE_OFF || previousState == BluetoothAdapter.STATE_TURNING_OFF) {
                if(state == BluetoothAdapter.STATE_ON || state == BluetoothAdapter.STATE_TURNING_ON) {
                    mCallaback.onBluetoothEnabled();
                } else if(state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    mCallaback.onBluetoothDisabled();
                }
            }
        }
        else {
            return;
        }
    }

    /**
     * Interface for this view.
     */
    public static interface BroadcastCallback {
        public void onBluetoothEnabled();
        public void onBluetoothDisabled();
    }

    /**
     * Helper method to register this receiver and return it for future unregister.
     * @param c the context.
     * @param callback the callback to notify when bluetooth is enabled.
     * @return an instance of the BluetoothBroadcastReceiver
     */
    public static BluetoothBroadcastReceiver register(Context c, BroadcastCallback callback) {
        BluetoothBroadcastReceiver bbr = new BluetoothBroadcastReceiver(callback);
        c.registerReceiver(bbr,  getIntentFilter());
        return bbr;
    }

    /**
     * Helper method that returns an intent filter for this receiver.
     * @return
     */
    private static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    /**
     * Helper method to unregister the receiver. Prevents an illegal arguments exception
     * @param c the context where the receiver was registered.
     * @param receiver the receiver that was previously registered.
     */
    public static void safeUnregister(Context c, BroadcastReceiver receiver) {
        try {
            c.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            Log.w("Error", "This receiver was not registered");
        }
    }
}
