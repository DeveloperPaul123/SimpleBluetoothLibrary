package com.devpaul.bluetoothutilitydemo;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.Toast;

import com.devpaul.bluetoothutillib.abstracts.BaseBluetoothActivity;

/**
 * Created by Pauly D on 3/18/2015.
 */
public class TestActivity extends BaseBluetoothActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBluetoothEnabled() {
        super.onBluetoothEnabled();
        Toast.makeText(this, "BT Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceSelected(String macAddress) {
        super.onDeviceSelected(macAddress);
        Toast.makeText(this, "Device " + macAddress, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBluetoothDataReceived(byte[] bytes, String data) {

    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {

    }

    @Override
    public void onDiscoveryStarted() {

    }

    @Override
    public void onDiscoveryFinished() {

    }
}
