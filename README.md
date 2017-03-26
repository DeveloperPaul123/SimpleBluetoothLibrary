SimpleBluetoothLibrary
======================
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-SimpleBluetoothLibrary-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1680)

[![](https://jitpack.io/v/DeveloperPaul123/SimpleBluetoothLibrary.svg)](https://jitpack.io/#DeveloperPaul123/SimpleBluetoothLibrary)

## Description

This library makes it easy for you to implement bluetooth in your Android app. The SimpleBlueooth class handles all the hard work for you and all you have to do is make a few method calls. 

## Dependency
````java
repositories {
    ....
    maven {url "https://jitpack.io"}
}
dependencies {
    ....
    compile  'com.github.DeveloperPaul123:SimpleBluetoothLibrary:1.5.1'
}
````

## Requirements
Min SDK Level is 14 or Android IceCreamSandwich

## Usage
This library provides a `BaseBluetoothActivity` that you inherit from to easily take care of:
- Enabling bluetooth
- Scanning for new devices.
- Connecting to a device.
- Receiving data from the device.
- Sending data to the device.

As an example:

````java
public class TestActivity extends BaseBluetoothActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set your content view here. 
    }

    @Override
    public void onBluetoothEnabled() {
        super.onBluetoothEnabled();
        //this is always called, when bluetooth is enabled.
        //by default the activity will start a scan. If you don't want this
        //delete the super call. 
    }

    @Override
    public void onDeviceSelected(String macAddress) {
        super.onDeviceSelected(macAddress);
        //this is called when a device is selected from the scan activity. By default,
        //the selected device will be connected to. If you don't want this, delete the super call.
    }

    @Override
    public void onBluetoothDataReceived(byte[] bytes, String data) {
       //called when data is received from the device. 
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {

    }

    @Override
    public void onDiscoveryStarted() {

    }

    @Override
    public void onDiscoveryFinished() {

    }
}
````

Alternatively you can use the ````SimpleBluetooth```` class yourself:

````java

public class MainActivity extends Activity {

    private SimpleBluetooth simpleBluetooth;
    private static final int SCAN_REQUEST = 119;
    private static final int CHOOSE_SERVER_REQUEST = 120;
    //...other code....//
    private String curMacAddress;

    @Override
    protected void onResume() {
        super.onResume();
        simpleBluetooth = new SimpleBluetooth(this, new SimpleBluetoothListener() {

            @Override
            public void onBluetoothDataReceived(byte[] bytes, String data) {
                //read the data coming in.
            }

            @Override
            public void onDeviceConnected(BluetoothDevice device) {
                //a device is connected so you can now send stuff to it
                
            }

            @Override
            public void onDeviceDisconnected(BluetoothDevice device) {
                // device was disconnected so connect it again?
               
            }

            @Override
            public void onDiscoveryStarted() {

            }

            @Override
            public void onDiscoveryFinished() {

            }
        });
        simpleBluetooth.initializeSimpleBluetooth();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //...other code...//
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.scan) {
            simpleBluetooth.scan(SCAN_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SCAN_REQUEST || requestCode == CHOOSE_SERVER_REQUEST) {

            if(resultCode == RESULT_OK) {

                curMacAddress = data.getStringExtra(DeviceDialog.DEVICE_DIALOG_DEVICE_ADDRESS_EXTRA);
                if(requestCode == SCAN_REQUEST) {
                    simpleBluetooth.connectToBluetoothDevice(curMacAddress);
                } else {
                    simpleBluetooth.connectToBluetoothServer(curMacAddress);
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        simpleBluetooth.endSimpleBluetooth();
    }
}
````

Finally, the library allows for an alternative way to receive data. Say you're using an Arduino or something similar that is sending out a newline character at the end of every loop. You can read this data using a buffered input type through the following line:

````java
simpleBluetooth.setInputStreamType(BluetoothUtility.InputStreamType.BUFFERED);
````

This will case the input type to change and you should receieve the data line by line. 

Additionally, this library allows for the creation and connection to a bluetooth server. Simply call:
````java
simpleBluetooth.createBluetoothServerConnection();
````
and on the connecting device call:
````java
//curMacAddress is the address of the server device.
simpleBluetooth.connectToBluetoothServer(curMacAddress);
````
Finally, the library can handle A2DP protocols, although this has not yet been tested. 

## Developed By

**Paul T**

Credit to **afollestad** for his material-dialog [library](https://github.com/afollestad/material-dialogs)

<h2>License</h2>

Copyright 2014 - 2017 Paul T

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.



