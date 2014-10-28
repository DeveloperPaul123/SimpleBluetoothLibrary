package com.devpaul.bluetoothutillib.handlers;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Paul Tsouchlos
 * A handler for receiving messages from the bluetooth device.
 */
public abstract class BluetoothHandler extends Handler {
    public static final int MESSAGE_READ = 121;
    public static final int MESSAGE_WAIT_FOR_CONNECTION = 143;
    public static final int MESSAGE_CONNECTION_MADE = 155;
    public static final int MESSAGE_A2DP_PROXY_RECEIVED = 157;
    public abstract void handleMessage(Message message);
}
