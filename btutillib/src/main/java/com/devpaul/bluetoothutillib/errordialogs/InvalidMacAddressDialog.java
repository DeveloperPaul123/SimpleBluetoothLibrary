package com.devpaul.bluetoothutillib.errordialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;

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
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity())
                .content("Invalid mac address.")
                .title("Error")
                .positiveText("Ok")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                });
        return dialog.build();
    }
}
