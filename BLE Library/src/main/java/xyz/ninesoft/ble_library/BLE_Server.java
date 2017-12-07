package xyz.ninesoft.ble_library;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by illiacDev on 2017-12-06.
 */

public class BLE_Server {
    public interface IOUT{
        void onRecive(String data);
    }

    final IOUT iout;
    String TAG = "BLE";
    final Context context;
    private BluetoothGattServer mGattServer;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private ArrayList<BluetoothGattService> mAdvertisingServices;

    private BluetoothDevice mConnectedDevice;

    private boolean isAdvertising;
    private boolean isDeviceSet = false;

    private List<ParcelUuid> mServiceUuids;

    public BLE_Server(IOUT iout, Context context) {
        this.iout = iout;
        this.context = context;

        isAdvertising = false;

        mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        //bluetoothAdapter.setName(BLUETOOTH_ADAPTER_NAME);
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        mAdvertisingServices = new ArrayList<BluetoothGattService>();
        mServiceUuids = new ArrayList<ParcelUuid>();

//        btnAdv = (Button)findViewById(R.id.buttonAdvStart);
//        btnStopAdv = (Button)findViewById(R.id.buttonAdvStop);
//        btnSendData = (Button) findViewById(R.id.buttonSendServer);
//        tvServer = (TextView) findViewById(R.id.textViewServer);
//        etInput = (EditText) findViewById(R.id.editTextInputServer);
//        etInput.setText("Server");

        //adding service and characteristics
        BluetoothGattService firstService = new BluetoothGattService(UUID.fromString(BluetoothUtility.SERVICE_UUID_1), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic firstServiceChar = new BluetoothGattCharacteristic(
                UUID.fromString(BluetoothUtility.CHAR_UUID_1),
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE
        );

        firstService.addCharacteristic(firstServiceChar);

        mAdvertisingServices.add(firstService);
        mServiceUuids.add(new ParcelUuid(firstService.getUuid()));

    }

    public void startAdvertise() {
        if (isAdvertising) return;
//        enableBluetooth();
        startGattServer();

        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();

        dataBuilder.setIncludeTxPowerLevel(false); //necessity to fit in 31 byte advertisement
        dataBuilder.setIncludeDeviceName(true);
        for (ParcelUuid serviceUuid : mServiceUuids)
            dataBuilder.addServiceUuid(serviceUuid);

        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

        mBluetoothLeAdvertiser.startAdvertising(settingsBuilder.build(), dataBuilder.build(),
                                                advertiseCallback);
        isAdvertising = true;
    }

    //Check if bluetooth is enabled, if not, then request enable
    /*private void enableBluetooth() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Bluetooth NOT supported");
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }*/

    private void startGattServer() {
        mGattServer = mBluetoothManager.openGattServer(getApplicationContext(), gattServerCallback);
        for (int i = 0; i < mAdvertisingServices.size(); i++) {
            mGattServer.addService(mAdvertisingServices.get(i));
            Log.d(TAG, "uuid" + mAdvertisingServices.get(i).getUuid());
        }
    }


    //Stop ble advertising and clean up
    public void stopAdvertise() {
        if(!isAdvertising) return;
        mBluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        mGattServer.clearServices();
        mGattServer.close();
        mAdvertisingServices.clear();
        isAdvertising = false;
    }


    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings advertiseSettings) {
            String successMsg = "Advertisement command attempt successful";
            Log.d(TAG, successMsg);
        }

        @Override
        public void onStartFailure(int i) {
            String failMsg = "Advertisement command attempt failed: " + i;
            Log.e(TAG, failMsg);
        }
    };

    public BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange status=" + status + "->" + newState);
            mConnectedDevice = device;
            isDeviceSet = true;
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.d(TAG, "service added: " + status);
        }

        @Override
        public void onCharacteristicReadRequest(
                BluetoothDevice device,
                int requestId,
                int offset,
                BluetoothGattCharacteristic characteristic
        ) {
            Log.d(TAG, "onCharacteristicReadRequest requestId=" + requestId + " offset=" + offset);

            if (characteristic.getUuid().equals(UUID.fromString(BluetoothUtility.CHAR_UUID_1))) {
                characteristic.setValue("test");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                                         characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicWriteRequest(
                BluetoothDevice device,
                int requestId,
                BluetoothGattCharacteristic characteristic,
                boolean preparedWrite,
                boolean responseNeeded,
                int offset,
                byte[] value
        ) {
            if (value != null) {
                Log.d(TAG, "Data written: " + BluetoothUtility.byteArraytoString(value));

                final String tmp = BluetoothUtility.byteArraytoString(value);
                /*runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tvServer.setText(tmp);
                    }
                });*/
                iout.onRecive(tmp);

                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                                         value);
            } else
                Log.d(TAG, "value is null");
        }
    };

    private Context getApplicationContext() {
        return context;
    }

}
