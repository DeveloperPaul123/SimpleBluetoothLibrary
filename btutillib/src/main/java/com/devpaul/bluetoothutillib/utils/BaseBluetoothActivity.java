package com.devpaul.bluetoothutillib.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.devpaul.bluetoothutillib.SimpleBluetooth;
import com.devpaul.bluetoothutillib.broadcasts.FoundDeviceReceiver;
import com.devpaul.bluetoothutillib.dialogs.DeviceDialog;

/**
 * Created by Paul Tsouchlos
 */
public abstract class BaseBluetoothActivity extends Activity
        implements FoundDeviceReceiver.FoundDeviceReceiverCallBack,SimpleBluetoothListener {

    private SimpleBluetooth simpleBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        simpleBluetooth = new SimpleBluetooth(this, this);
        simpleBluetooth.initializeSimpleBluetooth();
        simpleBluetooth.setSimpleBluetoothListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == BluetoothUtility.REQUEST_BLUETOOTH) {
            onBluetoothEnabled();
        } else if(resultCode == RESULT_OK && requestCode == BluetoothUtility.REQUEST_BLUETOOTH_SCAN) {
            String macAddress = data.getStringExtra(DeviceDialog.DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA);
            simpleBluetooth.connectToBluetoothDevice(macAddress);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void onBluetoothEnabled();

    public void requestScan() {
        simpleBluetooth.scan();
    }

    public void requestScan(int requestCode) {
        simpleBluetooth.scan(requestCode);
    }

    public void cancelScan() {
        simpleBluetooth.cancelScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleBluetooth.endSimpleBluetooth();
    }

    public SimpleBluetooth getSimpleBluetooth() {
        return this.simpleBluetooth;
    }


}
