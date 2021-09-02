package com.pfa.pfaapp;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BTDeviceList extends ListActivity {

//    private static final String DEVICE_WE_WANT_TO_MATCH1 = "SPP-R310";
//    private static final String DEVICE_WE_WANT_TO_MATCH2 = "SPP-R410";

    private  ListView listView;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice>pairedDevices;
    static public final int REQUEST_CONNECT_BT = 0x2300;

        static private final int REQUEST_ENABLE_BT = 0x1000;

        static private BluetoothAdapter mBluetoothAdapter = null;

        static private ArrayAdapter<String> mArrayAdapter = null;

        static private ArrayAdapter<BluetoothDevice> btDevices = null;

        private static final UUID SPP_UUID = UUID
                .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
// UUID.fromString(“00001101-0000-1000-8000-00805F9B34FB”);

        static private BluetoothSocket mbtSocket = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setTitle("Bluetooth_Device");

            try {
                if (initDevicesList() != 0) {
                    this.finish();
                    return;
                }

            } catch (Exception ex) {
                this.finish();
                return;
            }

            IntentFilter btIntentFilter = new IntentFilter(
                    BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBTReceiver, btIntentFilter);
//            Con();
        }



    public static BluetoothSocket getSocket() {
            return mbtSocket;
        }

        private void flushData() {
            try {
                if (mbtSocket != null) {
                    mbtSocket.close();
                    mbtSocket = null;
                }

                if (mBluetoothAdapter != null) {
                    mBluetoothAdapter.cancelDiscovery();
                }

                if (btDevices != null) {
                    btDevices.clear();
                    btDevices = null;
                }

                if (mArrayAdapter != null) {
                    mArrayAdapter.clear();
                    mArrayAdapter.notifyDataSetChanged();
                    mArrayAdapter.notifyDataSetInvalidated();
                    mArrayAdapter = null;
                }

                finalize();

            } catch (Exception ex) {
            } catch (Throwable e) {
            }

        }
        private int initDevicesList() {

            flushData();

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(),
                        "Bluetooth not supported!!", Toast.LENGTH_LONG).show();
                return -1;
            }

            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
                Toast.makeText(this, "Discovering", Toast.LENGTH_SHORT).show();
            }

            mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                  android.R.layout.simple_list_item_1);
//                     R.layout.bluetooth_list);

            setListAdapter(mArrayAdapter);


            try {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                enableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);

                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } catch (Exception ex) {
                return -2;
            }

            Toast.makeText(getApplicationContext(),
                    "Getting all available Bluetooth Devices", Toast.LENGTH_SHORT)
                    .show();

            return 0;

        }

        @Override
        protected void onActivityResult(int reqCode, int resultCode, Intent intent) {
            super.onActivityResult(reqCode, resultCode, intent);

            Log.d("imagePath" , "onActivityResult = " + "BTDeviceList");

            switch (reqCode) {
                case REQUEST_ENABLE_BT:

                    if (resultCode == RESULT_OK) {
                        BA = BluetoothAdapter.getDefaultAdapter();

                        pairedDevices = BA.getBondedDevices();
                        ArrayList list = new ArrayList();

                        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());

                        Set<BluetoothDevice> btDeviceList = mBluetoothAdapter
                                .getBondedDevices();
                        final ArrayAdapter adapter = new  ArrayAdapter(this,android.R.layout.simple_list_item_1, list);

                        try {
                            if (btDeviceList.size() > 0) {

                                for (BluetoothDevice device : btDeviceList) {
//                                    if (btDeviceList.contains(device) == false) {
                                    String devName = device.getName();
                                    if (list.equals("SPP-R310") || list.equals("SPP-R410")) {


                                        btDevices.add(device);

                                        mArrayAdapter.add(device.getName() + "\n"
                                                + device.getAddress());
                                        mArrayAdapter.notifyDataSetInvalidated();
                                    }
                                }

                            }
                        } catch (Exception ex) {
                        }
                    }

                    break;
            }

            mBluetoothAdapter.startDiscovery();

        }

        private  BroadcastReceiver mBTReceiver = new BroadcastReceiver() {

            @SuppressLint("ResourceType")
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    try {
                        if (btDevices == null) {
                            btDevices = new ArrayAdapter<BluetoothDevice>(
                                    getApplicationContext(), android.R.id.text1);
                        }

                        if (btDevices.getPosition(device) < 0) {
                            String devName = device.getName();
                            if (devName.equals("SPP-R310")|| devName.equals("SPP-R410")){


                                btDevices.add(device);

                                mArrayAdapter.add(device.getName() + "\n"
                                        + device.getAddress() + "\n");
                                mArrayAdapter.notifyDataSetInvalidated();
                            }
                        }
                    } catch (Exception ex) {
// ex.fillInStackTrace();
                    }

                    if (mBTReceiver!=null){

                        unregisterReceiver(mBTReceiver);
                    }
                }
            }
        };

        @Override
        protected void onListItemClick(ListView l, View v, final int position,
                                       long id) {
            super.onListItemClick(l, v, position, id);

            if (mBluetoothAdapter == null) {
                return;//toast of not paired
            }

            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }

            Toast.makeText(
                    getApplicationContext(),
                    "Connecting to " + btDevices.getItem(position).getName() + ","
            + btDevices.getItem(position).getAddress(),
                    Toast.LENGTH_SHORT).show();

            Thread connectThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        boolean gotuuid = btDevices.getItem(position)
                                .fetchUuidsWithSdp();
                        UUID uuid = btDevices.getItem(position).getUuids()[0]
                                .getUuid();
                        mbtSocket = btDevices.getItem(position)
                                .createRfcommSocketToServiceRecord(uuid);

                        mbtSocket.connect();
                    } catch (IOException ex) {
                        runOnUiThread(socketErrorRunnable);
                        try {
                            mbtSocket.close();
                        } catch (IOException e) {
// e.printStackTrace();
                        }
                        mbtSocket = null;
                        return;
                    } finally {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                finish();

                            }
                        });
                    }
                }
            });

            connectThread.start();
        }

        private final Runnable socketErrorRunnable = new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "Cannot establish connection", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.startDiscovery();

            }
        };

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            super.onCreateOptionsMenu(menu);

            menu.add(0, Menu.FIRST, Menu.NONE, "Refresh Scanning");

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            super.onOptionsItemSelected(item);

            switch (item.getItemId()) {
                case Menu.FIRST:
                    initDevicesList();
                    break;
            }

            return true;
        }
}

