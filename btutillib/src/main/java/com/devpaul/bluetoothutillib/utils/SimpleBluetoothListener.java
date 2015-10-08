package com.devpaul.bluetoothutillib.utils;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Paul T
 *
 * Abstract class with methods for SimpleBluetooth callbacks.
 * No need to override everything.
 */
public abstract class SimpleBluetoothListener {

    public SimpleBluetoothListener() {
        super();
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public final boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final String toString() {
        return super.toString();
    }

    /**
     * Called when bluetooth data is receieved.
     * @param bytes the raw byte buffer
     * @param data the data as a string.
     */
    public void onBluetoothDataReceived(byte[] bytes, String data) {

    }

    /**
     * Called when a device is connected.
     * @param device
     */
    public void onDeviceConnected(BluetoothDevice device) {

    }

    /**
     * Called when a device is disconnected.
     * @param device
     */
    public void onDeviceDisconnected(BluetoothDevice device) {

    }

    /**
     * Called when discovery (scanning) is started.
     */
    public void onDiscoveryStarted() {

    }

    /**
     * Called when discovery is finished.
     */
    public void onDiscoveryFinished() {

    }

    /**
     * Called when a device is paired.
     * @param device the paired device.
     */
    public void onDevicePaired(BluetoothDevice device) {

    }

    /**
     * Called when a device is unpaired.
     * @param device the unpaired device.
     */
    public void onDeviceUnpaired(BluetoothDevice device) {

    }
}
