package com.devpaul.bluetoothutillib.utils;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Paul Tsouchlos
 */
public interface SimpleBluetoothListener {

    public void onBluetoothDataReceived(byte[] bytes, String data);
    public void onDeviceConnected(BluetoothDevice device);
    public void onDeviceDisconnected(BluetoothDevice device);
    public void onDiscoveryStarted();
    public void onDiscoveryFinished();
}
