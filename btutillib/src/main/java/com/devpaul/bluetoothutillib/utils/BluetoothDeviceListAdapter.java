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

    /**
     * List that will hold all the devices.
     */
    private List<BluetoothDevice> devices;

    /**
     * Context from the UI activity.
     */
    private Context mContext;

    /**
     * Resource Id of a list item layout.
     */
    private int resourceId;

    /**
     * Constructor for {@code BluetoothDeviceListAdapter}. Creates a new array adapter for the device dialog for when scanning
     * for a new device.
     * @param context the context of the UI activity.
     * @param resource resource id for the list item layout.
     * @param objects list of BluetoothDevices to display in the list.
     */
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

    /**
     * Returns a {@code BluetoothDevice} given a postion in the list.
     * @param position the position of the device in the list. This is 0 based.
     * @return a {@code BluetoothDevice}
     */
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
