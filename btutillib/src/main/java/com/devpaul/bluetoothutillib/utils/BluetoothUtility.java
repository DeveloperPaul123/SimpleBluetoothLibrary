package com.devpaul.bluetoothutillib.utils;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.devpaul.bluetoothutillib.errordialogs.InvalidMacAddressDialog;
import com.devpaul.bluetoothutillib.handlers.BluetoothHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Paul Tsouchlos
 * Class that handles all bluetooth adapter stuff and connecting to devices, establishing sockets
 * listening for connections and reading/writing data on the sockets.
 */
public class BluetoothUtility implements BluetoothProfile.ServiceListener {

    /**
     * Debug tag
     */
    private static final String TAG = "BluetoothUtility";

    /**
     * {@code BluetoothAdapter} that handles bluetooth methods.
     */
    private BluetoothAdapter bluetoothAdapter;

    /**
     * Context field.
     */
    private Context mContext;

    /**
     * Activity field, should be same as the context.
     */
    private Activity mActivity;

    /**
     * Current {@code BluetoothSocket}
     */
    private BluetoothSocket bluetoothSocket;

    /**
     * Current {@code BluetoothDevice}
     */
    private BluetoothDevice bluetoothDevice;

    /**
     * {@code UUID} for a normal device connection.
     */
    private static final UUID NORMAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    /**
     * {@code UUID} for a server device connection.
     */
    private static final UUID SERVER_UUID = UUID.fromString("03107005-0000-4000-8000-00805F9B34FB");

    /**
     * Name of the bluetooth server.
     */
    private static final String SERVER_NAME = "bluetoothServer";

    /*
    Threads that handle all the connections and accepting of connections and what happens after
    you're connected.
     */

    /**
     * {@code Thread} that handles a connected socket.
     */
    private ConnectedThread connectedThread;

    /**
     * {@code Thread} that handles connecting to a device.
     */
    private ConnectDeviceThread connectThread;

    /**
     * {@code Thread} that listens for an incoming connection.
     */
    private AcceptThread acceptThread;

    /**
     * {@code Thread} that connects a device to an already set up bluetooth server.
     */
    private ConnectDeviceToServerThread connectToServerThread;

    /**
     * {@code BluetoothHandler} that handles all the calls for a reading incoming data and other
     * messages.
     */
    private BluetoothHandler bluetoothHandler;

    /**
     * {@code BluetoothA2dp} object for setting up an A2DP connection.
     */
    private BluetoothA2dp mBluetoothA2DP;

    /**
     * Bluetooth Request constant.
     */
    public static final int REQUEST_BLUETOOTH = 1001;

    /**
     * Constructor for {@code BluetoothUtility} This class is a wrapper class for a {@code
     * BluetoothAdapter} and makes a lot of its functionality easier.
     * @param context context from the calling activity or fragment.
     * @param refActivity reference to the calling activity.
     * @param handler a handler for handling read data and other messages. See
     * {@link com.devpaul.bluetoothutillib.handlers.BluetoothHandler} for more information.
     */
    public BluetoothUtility(Context context, Activity refActivity, BluetoothHandler handler) {
        //assign the fields.
        this.mContext = context;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            //bluetooth not supported.
            //TODO
        }
        this.mActivity = refActivity;
        this.bluetoothHandler = handler;
    }

    /**
     * Helper method that finds a device given its name.
     * @param name the name of the device.
     * @return a {@code BluetoothDevice} object if it was found. Returns null otherwise.
     */
    public BluetoothDevice findDeviceByName(String name) {
        for(BluetoothDevice device: getPairedDevices()) {
            if(device.getName().equalsIgnoreCase(name)) {
                return device;
            }
        }
        return null;
    }

    /**
     * Helper method that finds a device given its mac address.
     * @param macAddress the mac address of the device.
     * @return a {@code BluetoothDevice} object if it was found. Returns null otherwise.
     */
    public BluetoothDevice findDeviceByMacAddress(String macAddress) {
        for(BluetoothDevice device: getPairedDevices()) {
            if(device.getAddress().equalsIgnoreCase(macAddress)) {
                return device;
            }
        }
        return null;
    }

    /**
     * Returns all the paired devices on this device.
     * @return an ArrayList of the paired devices.
     */
    public ArrayList<BluetoothDevice> getPairedDevices() {
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> bonds = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device: bonds) {
            devices.add(device);
        }
        return devices;
    }

    /**
     * Enables the device to be discoverable for a certain duration.
     * @param duration the duration in milliseconds.
     */
    public void enableDiscovery(int duration) {
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        mActivity.startActivity(discoverableIntent);
    }

    /**
     * Checks to see if bluetooth is enabled.
     * @return boolean, true if it is enabled.
     */
    public boolean checkIfEnabled() {
        if(bluetoothAdapter != null) {
            return bluetoothAdapter.isEnabled();
        } else {
            return false;
        }
    }

    /**
     * Enables Bluetooth, BluetoothBroadCastReceiver should be used if you want to see the result.
     */
    public void enableBluetooth() {
        if(bluetoothAdapter != null) {
            if(!checkIfEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBluetooth, REQUEST_BLUETOOTH);
            }
        }
    }

    /**
     * Connects a device given a mac address.
     * @param macAddress the mac address of the device.
     */
    public void connectDevice(String macAddress) {
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            //check if bluetooth is enabled just in case.
            if(checkIfEnabled()) {
                //cancel all running threads.
                if(connectedThread != null) {
                    connectedThread.cancel();
                }
                if(connectThread != null) {
                    connectThread.cancel();
                }
                if(acceptThread != null) {
                    acceptThread.cancel();
                }
                if(connectToServerThread != null) {
                    connectToServerThread.cancel();
                }
                //check the mac address first.
                if(bluetoothAdapter.checkBluetoothAddress(macAddress)) {
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
                    connectThread = new ConnectDeviceThread(bluetoothDevice);
                    connectThread.start();
                } else {
                    //mac address not valid.
                    InvalidMacAddressDialog imad = InvalidMacAddressDialog.newInstance();
                    imad.show(mActivity.getFragmentManager(), "ERROR");
                }

            }
        }
    }

    /**
     * Starts a scan for nearby discoverable devices.
     */
    public void startScan() {
        boolean check = bluetoothAdapter.startDiscovery();
        if(!check) {
            //TODO
        }
    }

    /*
    Methods for A2DP connections. A2DP is meant for high quality audio transfer and is typically
    used for bluetooth headsets and speakers. The bluetooth module or device you are trying to
    connect to should be using the A2DP profile otherwise these methods will do nothing. A2DP is
    not the same as GATT or Smart Bluetooth and you shouldn't try to connect to these types of devices
    using an A2DP proxy.
     */

    /**
     * Gets the profile proxy for an A2DP connection.
     */
    public void setUpA2DPConnection() {
        bluetoothAdapter.getProfileProxy(mContext, this, BluetoothProfile.A2DP);
    }

    @Override
    public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
        bluetoothHandler
                .obtainMessage(BluetoothHandler.MESSAGE_A2DP_PROXY_RECEIVED,(BluetoothA2dp) bluetoothProfile)
                .sendToTarget();
    }

    @Override
    public void onServiceDisconnected(int i) {
        //restart the connection.
        setUpA2DPConnection();
    }

    /**
     * After a proxy is returned, this method connects the a2dp device using the proxy and the
     * class declared method.
     * @param bluetoothA2dp an A2DP proxy
     * @param a2dpDevice a {@code BluetoothDevice} that is an A2DP device.
     */
    public void connectA2DPProxy(BluetoothA2dp bluetoothA2dp, BluetoothDevice a2dpDevice) {
        if(bluetoothA2dp == null) {
            throw new NullPointerException("A2DP Proxy cannot be null!");
        }else {
            mBluetoothA2DP = bluetoothA2dp;
        }
        Method connect = null;
        try {
            connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //try to connect if our method isn't null.
        if(connect != null) {
            try {
                //try to run the method and connect to the device.
                connect.setAccessible(true);
                if(a2dpDevice != null) {
                    connect.invoke(bluetoothA2dp, a2dpDevice);
                } else {
                    Log.w(TAG, "Couldn't connect device.");
                }
            }catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


    }

    /*
    End methods for A2DP connections
     */


    /**
     * Closes all the connections.
     */
    public void closeConnections() {
        /*
        Close all threads.
         */
        if(connectThread != null) {
            connectThread.cancel();
        }
        if(connectedThread != null) {
            connectedThread.cancel();
        }
        if(acceptThread != null) {
            acceptThread.cancel();
        }
        if(connectToServerThread != null) {
            connectToServerThread.cancel();
        }
        /*
        Close the proxy(s) that are being used.
         */
        if(mBluetoothA2DP != null) {
            bluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetoothA2DP);
        }
    }

    /**
     * Sends a string of data to the bluetooth device.
     * @param data the string to send.
     */
    public void sendData(String data) {
        //check to see if the socket is connected first.
        if(bluetoothSocket != null) {
            if(!bluetoothSocket.isConnected()){
                if(bluetoothDevice != null) {
                    //Device not connected so connect again
                    Toast.makeText(mContext, "Connecting...", Toast.LENGTH_SHORT).show();
                    connectDevice(bluetoothDevice.getAddress());
                }
                else {
                    Toast.makeText(mContext, "Not connected!", Toast.LENGTH_SHORT).show();
                }
            } else {
                //is connected...
                if(connectedThread != null) {
                    connectedThread.write(data);
                }

            }
        }
    }

    /**
     * Cancels the scanning process.
     */
    public void cancelScan() {
        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * Sends a integer to the bluetooth device.
     * @param number the integer to send.
     */
    public void sendData(int number) {
      if(connectedThread != null) {
          connectedThread.write(number);
      }
    }

    /**
     * Connects to a set up server socket.
     * @param macAddress the mac address of the device hosting the server socket.
     */
    public void connectToClientToBluetoothServer(String macAddress) {
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            //check if bluetooth is enabled just in case.
            if(checkIfEnabled()) {
                //cancel all running threads.
                if(connectedThread != null) {
                    connectedThread.cancel();
                }
                if(connectThread != null) {
                    connectThread.cancel();
                }
                if(connectToServerThread != null) {
                    connectToServerThread.cancel();
                }
                //check the mac address first.
                if(bluetoothAdapter.checkBluetoothAddress(macAddress)) {
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
                    connectToServerThread = new ConnectDeviceToServerThread(bluetoothDevice);
                    connectToServerThread.start();
                } else {
                    //mac address not valid.
                    InvalidMacAddressDialog imad = InvalidMacAddressDialog.newInstance();
                    imad.show(mActivity.getFragmentManager(), "ERROR");
                }

            }
        }
    }

    /**
     * Creates a server socket on this device that awaits for a client connection.
     */
    public void createBluetoothServerSocket() {
        bluetoothAdapter.cancelDiscovery();
        if(connectToServerThread != null) {
            connectToServerThread.cancel();
        }
        if(connectedThread != null) {
            connectedThread.cancel();
        }
        if(connectThread != null) {
            connectThread.cancel();
        }

        acceptThread = new AcceptThread();
        acceptThread.start();

    }

    /**
     * Thread used to accept incoming connections and initiate a server socket.
     */
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, SERVER_UUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;

            if(bluetoothHandler != null) {
                bluetoothHandler
                        .obtainMessage(BluetoothHandler.MESSAGE_WAIT_FOR_CONNECTION)
                        .sendToTarget();
            }
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                        if(bluetoothHandler != null) {
                            bluetoothHandler
                                    .obtainMessage(BluetoothHandler.MESSAGE_CONNECTION_MADE)
                                    .sendToTarget();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
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
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Device not available.", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
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
            } catch (IOException e) { }

            mInputStream = tmpIn;
            mOutputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mInputStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    bluetoothHandler.obtainMessage(BluetoothHandler.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
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
                    mOutputStream.write(string.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
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

    /**
     * Used to connect a device to a server socket.
     */
    private class ConnectDeviceToServerThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectDeviceToServerThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(SERVER_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Device not available.", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    mmSocket.close();
                } catch (IOException closeException) {

                }
                return;
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
}
