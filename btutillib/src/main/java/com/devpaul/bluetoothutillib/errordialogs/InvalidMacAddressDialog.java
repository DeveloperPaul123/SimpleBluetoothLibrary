package com.devpaul.bluetoothutillib.errordialogs;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Paul Tsouchlos
 */
public class InvalidMacAddressDialog extends DialogFragment {

    public static InvalidMacAddressDialog newInstance() {
        InvalidMacAddressDialog invalidMacAddressDialog = new InvalidMacAddressDialog();
        return invalidMacAddressDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("Invalid mac address.")
                .setTitle("Error")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }
}
