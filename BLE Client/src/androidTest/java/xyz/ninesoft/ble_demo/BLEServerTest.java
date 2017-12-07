package xyz.ninesoft.ble_demo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.ninesoft.ble_library.BLE_Client;
import xyz.ninesoft.ble_library.BLE_Server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by illiacDev on 2017-12-06.
 */
@RunWith(AndroidJUnit4.class)
public class BLEServerTest {
    @Test
    public void ble_server() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("xyz.ninesoft.ble_demo", appContext.getPackageName());

        BLE_Server ble_Server = new BLE_Server(iout, appContext);
        ble_Server.startAdvertise();
    }

    @Test
    public void ble_client() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        BLE_Server ble_Server = new BLE_Server(appContext);
//        ble_Server.startAdvertise();
//        Thread.sleep(2000);
        BLE_Client ble_client = new BLE_Client(iout, appContext);
        ble_client.scanStart();

        Thread.sleep(5000);
    }

    @Test
    public void find_client() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        BLE_Client ble_client = new BLE_Client(iout, appContext);
        ble_client.scanStart();

        Thread.sleep(2000);

        BluetoothDevice device = ble_client.getDevice(ble_client.defaultBLE);
        if (device != null) {

            Log.i("BLE", "find_client: ");
        } else
            fail();

    }


    @Test
    public void connect_client() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        BLE_Client ble_client = new BLE_Client(iout, appContext);
        ble_client.scanStart();

        Thread.sleep(2000);

        BluetoothDevice device = ble_client.getDevice(ble_client.defaultBLE);
        if (device != null) {

            Log.i("BLE", "find_client: ");

            ble_client.connect(device.getAddress());
            Thread.sleep(2000);

        } else
            fail();
    }

}