package com.devpaul.bluetoothutillib.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devpaul.bluetoothutillib.R;

import java.util.List;

/**
 * Created by Paul Tsouchlos
 */
public class BluetoothDeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<BluetoothDevice> devices;
    private Context mContext;
    private int resourceId;


    public BluetoothDeviceListAdapter(Context context, int resource, List<BluetoothDevice> objects) {
        super(context, resource, objects);
        this.devices = objects;
        this.resourceId = resource;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public BluetoothDevice getItem(int position) {
        return devices.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            //inflate view because it is null.
            convertView = LayoutInflater.from(mContext).inflate(mContext.getResources()
                    .getLayout(R.layout.devpaul_bluetooth_list_item_layout), null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.bluetooth_device_name);
        TextView address = (TextView) convertView.findViewById(R.id.bluetooth_device_address);

        name.setText(devices.get(position).getName());
        address.setText(devices.get(position).getAddress());

        return convertView;
    }
}
