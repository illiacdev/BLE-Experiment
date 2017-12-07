package xyz.ninesoft.ble_library;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by illiacDev on 2017-12-06.
 */

public class BLE_Client {

    public interface IOUT {
        void onLeScan(final BluetoothDevice device);
    }

    final IOUT iout;


    String TAG = "BLE";
    //    public String defaultBLE = "59:08:56:E9:CC:20";
    public String defaultBLE = "52:91:E9:C0:DC:A8";
    final Context context;

    public final static int SCAN_PERIOD = 2000;

    private boolean isScanning;

//    private Button btnScan;
//    private Button btnStop;
//    private Button btnConnect;
//    private Button btnSend;
//    private EditText etInput;
//    private TextView tvClient;
//    private Spinner spnCentralList;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    public List<BluetoothDevice> getDeviceList() {
        return mDeviceList;
    }

    private List<BluetoothDevice> mDeviceList;
    private ArrayList<String> mDeviceNameList;
//    private static ArrayAdapter<String> centralAdapter;

//    private Handler mHandler;

    private BluetoothGatt mBtGatt = null;
    private BluetoothGattService mGattService = null;
    BluetoothGattCharacteristic mGattCharacteristic = null;

    private int mConnectionState = BluetoothProfile.STATE_DISCONNECTED;

    public BLE_Client(IOUT iout, Context context) {
        this.iout = iout;
        this.context = context;

        isScanning = false;

        mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(
                Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

//        btnScan = (Button)findViewById(R.id.buttonScan);
//        btnStop = (Button)findViewById(R.id.buttonStopScan);
//        btnSend = (Button) findViewById(R.id.buttonSendClient);
//        btnConnect = (Button) findViewById(R.id.buttonConnect);
//        spnCentralList = (Spinner) findViewById(R.id.spinnerCentralList);
//        etInput = (EditText) findViewById(R.id.editTextInputClient);
//        tvClient = (TextView) findViewById(R.id.textViewClient);
//        etInput.setText("Client");
//        btnConnect.setEnabled(false);

        mDeviceNameList = new ArrayList<String>();
//        centralAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mDeviceNameList);
//        centralAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnCentralList.setAdapter(centralAdapter);
        mDeviceList = new ArrayList<BluetoothDevice>();

    }

    public void scanStart() {
        mDeviceNameList.clear();
        mDeviceList.clear();

//        centralAdapter.clear();
//        centralAdapter.notifyDataSetChanged();

        startBleScan();
    }

    /**
     * BLE Scanning
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void startBleScan() {
        if (isScanning) return;
//        enableBluetooth();
        isScanning = true;

        // Stops scanning after a pre-defined scan period.
       /* mHandler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                isScanning = false;

                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                btnScan.setEnabled(true);
                btnStop.setEnabled(false);
            }
        }, SCAN_PERIOD);*/

        mBluetoothAdapter.startLeScan(mLeScanCallback);

//        btnScan.setEnabled(false);
//        btnStop.setEnabled(true);
        Log.d(TAG, "Bluetooth is currently scanning...");
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public void stopBleScan() {
        if (!isScanning) return;
        isScanning = false;

//        mBluetoothAdapter.stopLeScan(mLeScanCallback);

//        btnScan.setEnabled(true);
//        btnStop.setEnabled(false);
        Log.d(TAG, "Scanning has been stopped");
    }

    // Device scan callback for previous sdk version
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {
            String deviceInfo = device.getName() + " - " + device.getAddress();
            if (mDeviceNameList.contains(deviceInfo)) return;

            mDeviceNameList.add(deviceInfo);
            mDeviceList.add(device);

            iout.onLeScan(device);
            Log.d(TAG, "Device: " + deviceInfo + " Scanned!");
           /* runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    centralAdapter.notifyDataSetChanged();
                }
            });

            btnConnect.setEnabled(true);*/
            //stop scanning after we find ble device
//            stopBleScan();
//            btnScan.setEnabled(true);
//            btnStop.setEnabled(false);
        }
    };

    private Context getApplicationContext() {
        return context;
    }

    public BluetoothDevice getDevice(String mac_addr) {
        for (BluetoothDevice device : mDeviceList) {
            Log.i(TAG, "getDevice: " + device.getAddress());
            if (device.getAddress().equals(mac_addr)) {
                Log.i(TAG, "find getDevice: " + device.getAddress());
                return device;
            }
        }
        return null;
    }

    public void connect(String msc_addr) {
        BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(msc_addr);

        if (mBtGatt != null)
            mBtGatt.close();

        mBtGatt = remoteDevice.connectGatt(getApplicationContext(), false, mGattCallback);

    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState = BluetoothProfile.STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");

                if (gatt != null) {
                    mBtGatt = gatt;
                    if (gatt.discoverServices()) Log.d(TAG, "Attempt to discover Service");
                    else Log.d(TAG, "Failed to discover Service");
                } else Log.d(TAG, "btGatt == null");

            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                mConnectionState = BluetoothProfile.STATE_CONNECTING;
                Log.i(TAG, "Connecting GATT server.");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState = BluetoothProfile.STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                mBtGatt.close();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                mConnectionState = BluetoothProfile.STATE_DISCONNECTING;
                Log.i(TAG, "Disconnecting from GATT server.");
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services Discovered successfully : " + status);

                List<BluetoothGattService> gattServices = gatt.getServices();
                mBtGatt = gatt;
                if (gattServices.size() > 0) {
                    Log.d(TAG, "Found : " + gattServices.size() + " services");
                    for (BluetoothGattService bluetoothGattService : gattServices)
                        Log.d(TAG, "UUID = " + bluetoothGattService.getUuid().toString());

                    BluetoothGattService gattServ = gatt.getService(
                            UUID.fromString(BluetoothUtility.SERVICE_UUID_1));
                    if (gattServ != null) {
                        mGattService = gattServ;
                        BluetoothGattCharacteristic gattChar = gattServ.getCharacteristic(
                                UUID.fromString(BluetoothUtility.CHAR_UUID_1));
                        mGattCharacteristic = gattChar;
                        gatt.readCharacteristic(gattChar);
                    } else
                        Log.d(TAG, "gattServ == null");
                } else
                    Log.d(TAG, "gattServices.size() == 0");
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }



        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic is read");

                gatt.setCharacteristicNotification(characteristic, true);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            if (data != null) {
                final String tmp = BluetoothUtility.byteArraytoString(data);
                Log.d(TAG, "Changed data : " + tmp);

                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvClient.setText(tmp);
                    }
                });*/
            } else
                Log.d(TAG, "Changed Data is null");
        }
    };

    public void sendData(String data) {
        if(mBtGatt != null) {
            mGattCharacteristic.setValue(data);
            if(mBtGatt.writeCharacteristic(mGattCharacteristic))
                Log.d(TAG, "Data sent");
            else
                Log.d(TAG, "Data not sent");
        }
    }

}
