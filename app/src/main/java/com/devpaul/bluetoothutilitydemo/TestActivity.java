package com.devpaul.bluetoothutilitydemo;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.Toast;

import com.devpaul.bluetoothutillib.abstracts.BaseBluetoothActivity;
import com.devpaul.bluetoothutillib.utils.SimpleBluetoothListener;

/**
 * Created by Pauly D on 3/18/2015.
 */
public class TestActivity extends BaseBluetoothActivity {

    @Override
    public SimpleBluetoothListener getSimpleBluetoothListener() {
        return new SimpleBluetoothListener() {
            @Override
            public void onBluetoothDataReceived(byte[] bytes, String data) {
                super.onBluetoothDataReceived(bytes, data);
            }

            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                super.onDeviceConnected(device);
            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device) {
                super.onDeviceDisconnected(device);
            }

            @Override
            public void onDiscoveryStarted() {
                super.onDiscoveryStarted();
            }

            @Override
            public void onDiscoveryFinished() {
                super.onDiscoveryFinished();
            }

            @Override
            public void onDevicePaired(BluetoothDevice device) {
                super.onDevicePaired(device);
            }

            @Override
            public void onDeviceUnpaired(BluetoothDevice device) {
                super.onDeviceUnpaired(device);
            }
        };
    }

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

}
