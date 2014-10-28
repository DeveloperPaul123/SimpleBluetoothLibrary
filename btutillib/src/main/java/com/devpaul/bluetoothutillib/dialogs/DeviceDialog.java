package com.devpaul.bluetoothutillib.dialogs;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.devpaul.bluetoothutillib.R;
import com.devpaul.bluetoothutillib.broadcasts.BluetoothBroadcastReceiver;
import com.devpaul.bluetoothutillib.broadcasts.BluetoothStateReceiver;
import com.devpaul.bluetoothutillib.broadcasts.FoundDeviceReceiver;
import com.devpaul.bluetoothutillib.handlers.BluetoothHandler;
import com.devpaul.bluetoothutillib.utils.BaseBluetoothActivity;
import com.devpaul.bluetoothutillib.utils.BluetoothDeviceListAdapter;
import com.devpaul.bluetoothutillib.utils.BluetoothUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for choosing a paired bluetooth device or scanning for new, available devices.
 */
public class DeviceDialog extends BaseBluetoothActivity {

    /**
     * List view for the devices.
     */
    private ListView listView;

    /**
     * Utility that handles the bluetooth stuff.
     */
    private BluetoothUtility bluetoothUtility;

    /**
     * List of devices.
     */
    private List<BluetoothDevice> devices;

    /**
     * List adapter for the list view.
     */
    private BluetoothDeviceListAdapter bdla;

    /**
     * Broadcast receiver for bluetooth enabled.
     */
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;

    /**
     * Broadcast receiver for when device is found during scan.
     */
    private FoundDeviceReceiver foundDeviceReceiver;

    /**
     * Broadcast receiver for changes in bluetooth states.
     */
    private BluetoothStateReceiver bluetoothStateReceiver;

    /**
     * Button that starts scanning.
     */
    private Button scanButton;

    /**
     * Constant name for retrieving bluetooth device from this activity in on activity result.
     */
    public static final String DEVICE_DIALOG_DEVICE_EXTRA = "deviceDialogDeviceExtra";

    /**
     * Constant name for retrieving bluetooth device address from this activity in on activity
     * result.
     */
    public static final String DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA = "deviceDialogDeviceAddressExtra";

    /**
     * Progress Dialog that is shown during scanning.
     */
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_dialog);
        //prepare the progress dialog.
        prepareProgressDialog();
        //list view for all the items.
        listView = (ListView) findViewById(android.R.id.list);
        //start the bluetooth utility.
        bluetoothUtility = new BluetoothUtility(this, this, new BluetoothHandler() {
            @Override
            public void handleMessage(Message message) {

            }
        });

        if(bluetoothUtility.checkIfEnabled()) {
            //bluetooth is enabled.
            populateList();
        } else {
            //enable bluetooth.
            bluetoothUtility.enableBluetooth();
        }
        //set up the button.
        scanButton = (Button) findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothUtility.startScan();
            }
        });

    }

    /**
     * Helper method to prepare the progress dialog for showing.
     */
    private void prepareProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Scanning...");
        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(bluetoothUtility != null) {
                    bluetoothUtility.cancelScan();
                    progressDialog.dismiss();
                }
            }
        });
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register the broadcast receivers for this activity.
        bluetoothBroadcastReceiver = BluetoothBroadcastReceiver.register(this, this);
        foundDeviceReceiver = FoundDeviceReceiver.register(this, this);
        bluetoothStateReceiver = BluetoothStateReceiver.register(this, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister the receivers.
        BluetoothBroadcastReceiver.safeUnregister(this, bluetoothBroadcastReceiver);
        FoundDeviceReceiver.safeUnregister(this, foundDeviceReceiver);
        BluetoothStateReceiver.safeUnregister(this, bluetoothStateReceiver);
    }

    /**
     * Helper method that populates the list with the bonded devices from this device.
     */
    private void populateList() {
        devices = new ArrayList<BluetoothDevice>();
        devices = bluetoothUtility.getPairedDevices();
        bdla = new BluetoothDeviceListAdapter(this,
                R.layout.devpaul_bluetooth_list_item_layout, devices);
        listView.setAdapter(bdla);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothDevice selectedDevice = devices.get(i);
                Intent data = new Intent();
                data.putExtra(DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA, selectedDevice.getAddress());
                data.putExtra(DEVICE_DIALOG_DEVICE_EXTRA, selectedDevice);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public void onBluetoothEnabled() {
        populateList();
    }

    @Override
    public void onBluetoothDisabled() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        bdla.add(device);
        bdla.notifyDataSetChanged();
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        //shouldn't ever be called here.
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        //shouldn't ever be called here.
    }

    @Override
    public void onDiscoveryFinished() {
        if(progressDialog != null) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onDiscoveryStarted() {
        if(progressDialog != null) {
            progressDialog.show();
        }
    }
}
