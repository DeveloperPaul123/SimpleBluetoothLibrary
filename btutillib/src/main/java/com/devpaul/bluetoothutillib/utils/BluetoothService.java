package com.devpaul.bluetoothutillib.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.logging.Handler;

/**
 * Created by Paul Tsouchlos
 */
public class BluetoothService extends Service {

    private Handler mHandler;
    public BluetoothService(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
