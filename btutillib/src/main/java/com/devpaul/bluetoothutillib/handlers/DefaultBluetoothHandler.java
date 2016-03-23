package com.devpaul.bluetoothutillib.handlers;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.os.Message;
import android.support.design.widget.Snackbar;

import com.devpaul.bluetoothutillib.utils.SimpleBluetoothListener;

/**
 * Created by Paul on 3/21/2016.
 *
 * Default handler for SimpleBluetooth.
 */
public class DefaultBluetoothHandler extends BluetoothHandler {

    /**
     * Default bluetooth handler for SimpleBluetooth. This should work fine for most cases.
     * Override if you need more message.what options.
     * @param listener the simple bluetooth listener.
     * @param context reference context.
     */
    public DefaultBluetoothHandler(SimpleBluetoothListener listener, Context context) {
        super(listener, context);
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) message.obj;
                //get how many bytes were actually read.
                int datalength = message.arg1;
                String readMessage = new String(readBuf, 0, datalength);
                if(readBuf.length > 0) {
                    if(mListener != null)
                        mListener.onBluetoothDataReceived(readBuf, readMessage);
                }
                break;
            case MESSAGE_WAIT_FOR_CONNECTION:
                if(dialog != null) {
                    dialog.setTitle("");
                    dialog.setMessage("Waiting...");
                    dialog.show();
                }
                break;
            case MESSAGE_CONNECTION_MADE:
                if(dialog != null) {
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                        if(shouldShowSnackbars && mActivity != null) {
                            Snackbar.make(mActivity.findViewById(android.R.id.content), "Device connected.",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case MESSAGE_A2DP_PROXY_RECEIVED:
                BluetoothA2dp device = (BluetoothA2dp) message.obj;
                if(device != null && mListener != null) {
                    mListener.onBluetoothA2DPRequested(device);
                }
                break;
            default:
                break;
        }
    }
}
