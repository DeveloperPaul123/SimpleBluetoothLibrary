package com.devpaul.bluetoothutillib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.widget.Toast;

import com.devpaul.bluetoothutillib.broadcasts.BluetoothBroadcastReceiver;
import com.devpaul.bluetoothutillib.broadcasts.BluetoothPairingReceiver;
import com.devpaul.bluetoothutillib.broadcasts.BluetoothStateReceiver;
import com.devpaul.bluetoothutillib.broadcasts.FoundDeviceReceiver;
import com.devpaul.bluetoothutillib.dialogs.DeviceDialog;
import com.devpaul.bluetoothutillib.handlers.BluetoothHandler;
import com.devpaul.bluetoothutillib.utils.BluetoothUtility;
import com.devpaul.bluetoothutillib.utils.SimpleBluetoothListener;

import static com.devpaul.bluetoothutillib.utils.BluetoothUtility.InputStreamType;

/**
 * Created by Paul Tsouchlos
 * Class for easily setting up bluetooth connections.
 */
public class SimpleBluetooth {

    /**
     * Receiver for the {@code BluetoothBroadcastReceiver}
     */
    private final BluetoothBroadcastReceiver.BroadcastCallback bluetoothBroadcastRecieverCallback
            = new BluetoothBroadcastReceiver.BroadcastCallback() {
        @Override
        public void onBluetoothEnabled() {
            initializeSimpleBluetooth();
        }

        @Override
        public void onBluetoothDisabled() {
            initializeSimpleBluetooth();
        }
    };

    /**
     * Receiver for the {@code BluetoothStateReceiver}
     */
    private final BluetoothStateReceiver.Callback stateRecieverCallback = new BluetoothStateReceiver.Callback() {
        @Override
        public void onDeviceConnected(BluetoothDevice device) {
            if(mListener != null) {
                mListener.onDeviceConnected(device);
            }
        }

        @Override
        public void onDeviceDisconnected(BluetoothDevice device) {
            if(mListener != null) {
                mListener.onDeviceDisconnected(device);
            }
        }

        @Override
        public void onDiscoveryFinished() {
            if(mListener != null) {
                mListener.onDiscoveryFinished();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            if(mListener != null) {
                mListener.onDiscoveryStarted();
            }
        }
    };

    private final BluetoothPairingReceiver.Callback bluetoothPairingReciever = new BluetoothPairingReceiver.Callback() {
        @Override
        public void onDevicePaired(BluetoothDevice device) {
            if(mListener != null) {
                mListener.onDevicePaired(device);
            }
        }

        @Override
        public void onDeviceUnpaired(BluetoothDevice device) {
            if(mListener != null) {
                mListener.onDeviceUnpaired(device);
            }
        }
    };

    /**
     * {@link com.devpaul.bluetoothutillib.utils.SimpleBluetoothListener} for SimpleBluetooth
     */
    private SimpleBluetoothListener mListener;

    /**
     * Context of the calling activity.
     */
    private Context mContext;

    /**
     * Reference to the calling activity.
     */
    private Activity mActivity;

    /**
     * {@code BluetoothUtility used by SimpleBluetooth}
     */
    private BluetoothUtility bluetoothUtility;

    /**
     * {@code BluetoothBroadcastReceiver} receives enable/disable changes
     */
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;

    /**
     * {@code BluetoothStateReceiver} that receives connection/disconnection intents.
     */
    private BluetoothStateReceiver bluetoothStateReceiver;

    /**
     * {@code BluetoothPairingReceiver} that receives pair/unpair intents.
     */
    private BluetoothPairingReceiver bluetoothPairingReceiver;

    /**
     * State boolean
     */
    private boolean isInitialized;

    /**
     * Progress dialog.
     */
    private ProgressDialog progressDialog;

    /**
     * A2DP device if used.
     */
    private BluetoothDevice a2dpDevice;

    /**
     * Alternative custom handler supplied by user.
     */
    private BluetoothHandler customHandler;

    /**
     * The input stream type to use for the bluetooth thread.
     */
    private InputStreamType curType;

    /**
     * Boolean for connecting with service.
     */
    private boolean connectWithService = false;

    /**
     * Constructor for {@code SimpleBluetooth}
     * Allows for easy handling for setting up connections and bluetooth servers to connect to.
     * @param context context from the calling activity
     * @param refActivity reference to the calling activity. Context and activity should match.
     */
    public SimpleBluetooth(Context context, Activity refActivity) {
        //initialize fields.
        this.progressDialog = new ProgressDialog(context);
        this.mContext = context;
        this.mActivity = refActivity;
        this.bluetoothUtility = new BluetoothUtility(mContext, mActivity, mHandler);
        //register the state change receiver.
        this.curType = InputStreamType.NORMAL;

        /*
        Trying onActivityResult instead of this for now.
         */
//        this.bluetoothBroadcastReceiver = BluetoothBroadcastReceiver
//                .register(mContext, bluetoothBroadcastRecieverCallback);
        this.bluetoothStateReceiver = BluetoothStateReceiver
                .register(mContext, stateRecieverCallback);

        this.bluetoothPairingReceiver = BluetoothPairingReceiver
                .register(mContext, bluetoothPairingReciever);
        //state boolean
        this.isInitialized = false;
    }

    /**
     * Constructor for {@code SimpleBluetooth} Use this constructor to provide your own custom bluetooth
     * handler.
     * @param context the context of the calling activity.
     * @param refActivity reference to the calling activity. Context and activity should match.
     * @param handler custom {@code BluetoothHandler} for bluetooth event call backs.
     */
    public SimpleBluetooth(Context context, Activity refActivity, BluetoothHandler handler) {
        //initialize fields.
        this.progressDialog = new ProgressDialog(context);
        this.mContext = context;
        this.mActivity = refActivity;
        this.customHandler = handler;
        this.curType = InputStreamType.NORMAL;
        //check the handler.
        if(customHandler == null) throw
                new NullPointerException("Custom BluetoothHandler cannot be null!");
        this.bluetoothUtility = new BluetoothUtility(mContext, mActivity, customHandler);
        //register the state change receiver.
        /*
        Trying onActivityResult instead of this method.
         */
//        this.bluetoothBroadcastReceiver = BluetoothBroadcastReceiver
//                .register(mContext, bluetoothBroadcastRecieverCallback);

        this.bluetoothStateReceiver = BluetoothStateReceiver
                .register(mContext, stateRecieverCallback);
        this.bluetoothPairingReceiver = BluetoothPairingReceiver
                .register(mContext, bluetoothPairingReciever);
        //state boolean
        this.isInitialized = false;
    }

    /**
     * Sets a simple bluetooth listener for this service. Use this in your activity to get back the
     * read data.
     * @param simpleBluetoothListener the new simple bluetooth listener
     */
    public void setSimpleBluetoothListener(SimpleBluetoothListener simpleBluetoothListener) {
        this.mListener = simpleBluetoothListener;
    }

    /**
     * Sets the input stream type for reading data from the bluetooth device.
     * @param type the {@code InputStreamType}, can either be Normal or Buffered.
     */
    public void setInputStreamType(InputStreamType type) {
        this.curType = type;
    }

    /**
     * Default handler for Simple bluetooth.
     */
    private BluetoothHandler mHandler = new BluetoothHandler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) message.obj;
                    String readMessage = new String(readBuf);
                    if(readBuf != null && readBuf.length > 0) {
                        if(mListener != null)
                            mListener.onBluetoothDataReceived(readBuf, readMessage);
                    }
                    break;
                case MESSAGE_WAIT_FOR_CONNECTION:
                    if(progressDialog != null) {
                        progressDialog.setTitle("");
                        progressDialog.setMessage("Waiting...");
                        progressDialog.show();
                    }
                    break;
                case MESSAGE_CONNECTION_MADE:
                    if(progressDialog != null) {
                        if(progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Toast.makeText(mContext, "Device connected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case MESSAGE_A2DP_PROXY_RECEIVED:
                    BluetoothA2dp bluetoothA2dp = (BluetoothA2dp) message.obj;
                    bluetoothUtility.connectA2DPProxy(bluetoothA2dp, a2dpDevice);
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * Method that must be called to set everything up for this service.
     */
    public boolean initializeSimpleBluetooth() {
        if(!bluetoothUtility.checkIfEnabled()) {
            bluetoothUtility.enableBluetooth();
        } else {
            isInitialized = true;
        }

        return isInitialized;
    }

    /**
     * Sends data to the connected device.
     * @param data The string of data to send.
     */
    public void sendData(String data) {
//        Log.d("SIMPLEBT", "Sending data: " + data);
        bluetoothUtility.sendData(data);
    }

    /**
     * Sends data to the connected device.
     * @param data the int to send.
     */
    public void sendData(int data) {
        bluetoothUtility.sendData(data);
    }


    /**
     * Starts the device dialog to get a device to connect to.
     * @param requestCode the request code for the intent. Use this to check against in
     *                    OnActivityResult.
     */
    public void scan(int requestCode) {
        Intent deviceDialog = new Intent(mActivity, DeviceDialog.class);
        mActivity.startActivityForResult(deviceDialog, requestCode);
    }

    /**
     * Performs a scan of devices directly and does not launch the device dialog. If you want to
     * receive found devices then you need to register the
     * {@link FoundDeviceReceiver} in the activity and you also
     * need the {@link FoundDeviceReceiver.FoundDeviceReceiverCallBack}.
     */
    public void scan() {
        bluetoothUtility.scan();
    }

    /**
     * Cancels the ongoing scan started by {@code scan()}
     */
    public void cancelScan() {
       bluetoothUtility.cancelScan();
    }

    /**
     * Connect to the bluetooth device knowing only the macAddress.
     * @param macAddress the mac address of the device. If this isn't valid, it won't connect.
     */
    public void connectToBluetoothDevice(String macAddress) {
        if(!isInitialized) {
            throw new IllegalStateException("Must initialize before using any other method in class" +
                    "SimpleBluetooth! Call initializeSimpleBluetooth()");
        } else {
            bluetoothUtility.connectDevice(macAddress);
        }

    }

    /**
     * Connect to a generic bluetooth device.
     * @param device Bluetooth device representing the device.
     */
    public void connectToBluetoothDevice(BluetoothDevice device) {
        if(!isInitialized) {
            throw new IllegalStateException("Must initialize before using any other method in class" +
                    "SimpleBluetooth! Call initializeSimpleBluetooth()");
        }

    }

    /**
     * Creates a bluetooth server on this device that awaits for a client to connect.
     */
    public void createBluetoothServerConnection() {
        if(!isInitialized) {
            throw new IllegalStateException("Must initialize before using any other method in class" +
                    "SimpleBluetooth! Call initializeSimpleBluetooth()");
        } else {
            bluetoothUtility.createBluetoothServerSocket();
        }

    }

    /**
     * Connects to a bluetooth server set up on another device.
     * @param macAddress the mac address of the server device. If this isn't valid, it won't connect.
     */
    public void connectToBluetoothServer(String macAddress) {
        if(!isInitialized) {
            throw new IllegalStateException("Must initialize before using any other method in class" +
                    "SimpleBluetooth! Call initializeSimpleBluetooth()");
        } else {
            bluetoothUtility.connectToClientToBluetoothServer(macAddress);
        }

    }


    /**
     * Connects to an A2DP device.
     * @param deviceName the name of the device to connect to.
     */
    public void connectToA2DPDevice(String deviceName) {
        if(!isInitialized) {
            throw new IllegalStateException("Must initialize before using any other method in class" +
                    "SimpleBluetooth! Call initializeSimpleBluetooth()");
        } else {
            a2dpDevice = bluetoothUtility.findDeviceByName(deviceName);
            bluetoothUtility.setUpA2DPConnection();
        }

    }

    /**
     * Makes the device discoverable to other devices for a certain amount of time.
     * @param duration the duration length in seconds.
     */
    public void makeDiscoverable(int duration) {
        bluetoothUtility.enableDiscovery(duration);
    }


    /**
     * Ends all connections and unregister the receiver.
     */
    public void endSimpleBluetooth() {
//        BluetoothBroadcastReceiver.safeUnregister(mContext, bluetoothBroadcastReceiver);
        BluetoothStateReceiver.safeUnregister(mContext, bluetoothStateReceiver);
        BluetoothPairingReceiver.safeUnregister(mContext, bluetoothPairingReceiver);
        bluetoothUtility.closeConnections();
    }

    /**
     * Gets the used bluetooth utility.
     * @return the {@code BluetoothUtility}
     */
    public BluetoothUtility getBluetoothUtility() {
        return this.bluetoothUtility;
    }
}
