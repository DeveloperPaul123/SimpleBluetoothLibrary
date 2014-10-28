package com.devpaul.bluetoothutillib.utils;

import android.app.Activity;

import com.devpaul.bluetoothutillib.broadcasts.BluetoothStateReceiver;
import com.devpaul.bluetoothutillib.handlers.BluetoothHandler;
import com.devpaul.bluetoothutillib.broadcasts.BluetoothBroadcastReceiver;
import com.devpaul.bluetoothutillib.broadcasts.FoundDeviceReceiver;

/**
 * Created by Paul Tsouchlos
 */
public abstract class BaseBluetoothActivity extends Activity
        implements BluetoothBroadcastReceiver.BroadcastCallback, FoundDeviceReceiver.FoundDeviceReceiverCallBack,
        BluetoothStateReceiver.Callback {

    public BluetoothHandler handler;
}
