package com.devpaul.bluetoothutillib.errordialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Paul Tsouchlos
 */
public class BluetoothDisabledErrorDialog extends DialogFragment {


    public static void showDialog(Context context) {

        BluetoothDisabledErrorDialog bluetoothDisabledErrorDialog =
                BluetoothDisabledErrorDialog.newInstance();
        bluetoothDisabledErrorDialog.show(((Activity) context).getFragmentManager(), "Error");

    }

    public static BluetoothDisabledErrorDialog newInstance() {
        BluetoothDisabledErrorDialog bded = new BluetoothDisabledErrorDialog();
        bded.setCancelable(false);
        return bded;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Bluetooth Disabled");
        dialog.setMessage("Bluetooth was disabled, would you like to re-enable.");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                getActivity().startActivity(intent);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return dialog.create();
    }
}
