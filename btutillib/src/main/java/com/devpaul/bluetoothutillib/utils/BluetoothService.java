package com.devpaul.bluetoothutillib.utils;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.devpaul.bluetoothutillib.handlers.BluetoothHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Paul T.
 *
 * Bluetooth service class so you can connect to bluetooth devices persistently in an app
 * background.
 */
public class BluetoothService extends Service {

    /**
     * Callback for the service.
     */
    public interface BluetoothServiceCallback {
        public void onDeviceConnected(BluetoothDevice device);
    }

    /**
     * Local binder
     */
    private LocalBinder mLocalBinder = new LocalBinder();

    /**
     * Bluetooth device.
     */
    private BluetoothDevice device;

    /**
     * BluetoothAdapter instance for the service.
     */
    private BluetoothAdapter adapter;

    /**
     * The connect device thread for connecting to a device.
     */
    private ConnectDeviceThread connectDeviceThread;

    /**
     * Connected thread for when handling when the device is connected.
     */
    private ConnectedThread connectedThread;

    /**
     * Bluetooth socket holder for the created socket.
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * Instance of a callback.
     */
    private BluetoothServiceCallback callback;

    /**
     * Notification Manager for if the service is sticky.
     */
    private NotificationManager notificationManager;

    /**
     * Handler for bluetooth.
     */
    private BluetoothHandler bluetoothHandler;

    /**
     * Stream type.
     */
    private BluetoothUtility.InputStreamType streamType;

    /**
     * Command for connecting to a device.
     */
    public static final String CONNECT_BLUETOOTH_DEVICE = "startListeningToSocket";

    /**
     * Address of device.
     */
    public static final String DEVICE_ADDRESS_EXTRA = "deviceAddressExtra";

    /**
     * Notification ID.
     */
    public static final int FOREGROUND_NOTIFICATION_ID = 1;

    /**
     * Stop service command.
     */
    public static final String STOP_SERVICE_ACTION = "stopBluetoothServiceAction";

    /**
     * {@code UUID} for a normal device connection.
     */
    private static final UUID NORMAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    /**
     * Write value to the connected device.
     * @param message
     */
    public void write(String message) {
        if(connectedThread != null) {
            if(connectedThread.isAlive()) {
                connectedThread.write(message);
            }
        }

    }

    /**
     * Used to connect a device a generic socket.
     */
    private class ConnectDeviceThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectDeviceThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final

            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(NORMAL_UUID);
            } catch (IOException e) {

            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            adapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            if(callback != null) {
                callback.onDeviceConnected(mmDevice);
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /**
     * Helper method for managing a connected socket.
     * @param mmSocket
     */
    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.start();
    }

    /**
     * Thread for when you're connected.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mInputStream;
        private final OutputStream mOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
//                Log.d("ConnectedThread", e.getMessage());
            }

            mInputStream = tmpIn;
            mOutputStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()
            BufferedReader reader;

            if(streamType == BluetoothUtility.InputStreamType.NORMAL) {
                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        bytes = mInputStream.available();
                        if(bytes > 0) {
                            buffer = new byte[bytes];
                            // Read from the InputStream
                            bytes = mInputStream.read(buffer);
                            // Send the obtained bytes to the UI activity
                            bluetoothHandler.obtainMessage(BluetoothHandler.MESSAGE_READ, bytes, -1, buffer)
                                    .sendToTarget();
                        }
                    } catch (IOException e) {
                        break;
                    }
                }
                //Buffered reader.
            } else {
                reader = new BufferedReader(new InputStreamReader(mInputStream));
                // Keep listening to the InputStream until an exception occurs
                while (true) {
                    try {
                        if(reader.ready()) {
                            String message = reader.readLine();
                            bluetoothHandler.obtainMessage(BluetoothHandler.MESSAGE_READ, -1, -1, message)
                                    .sendToTarget();
                        }
//                        bytes = mInputStream.available();
//                        if(bytes > 0) {
//                            buffer = new byte[bytes];
//                            // Read from the InputStream
//                            bytes = mInputStream.read(buffer);
//                            // Send the obtained bytes to the UI activity
//                            bluetoothHandler.obtainMessage(BluetoothHandler.MESSAGE_READ, bytes, -1, buffer)
//                                    .sendToTarget();
//                        }
                    } catch (IOException e) {
                        break;
                    }
                }
            }


        }

        /**
         * Called to send a string across the bluetooth socket.
         * @param string the string to send.
         */
        public void write(String string) {
            if(mOutputStream != null) {
                try {
//                    Log.d("ConnectedThread", "Writing data: " + string);
                    mOutputStream.write(string.getBytes());
                } catch (IOException e) {
//                    Log.d("ConnectedThread",e.getMessage());
                }
            }
        }

        /**
         * Called to send bytes across the bluetooth socket.
         * @param bytes the bytes to send.
         */
        public void write(byte[] bytes) {
            if(mOutputStream != null) {
                try {
                    mOutputStream.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(int i) {
            if(mOutputStream != null) {

                try {
                    mOutputStream.write(i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
