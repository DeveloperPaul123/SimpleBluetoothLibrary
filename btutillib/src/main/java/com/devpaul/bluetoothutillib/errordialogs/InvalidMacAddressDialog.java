package com.devpaul.bluetoothutillib.errordialogs;

import android.app.AlertDialog;
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Invalid Mac Address");
        dialog.setTitle("Error");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        return dialog.create();
    }
}
