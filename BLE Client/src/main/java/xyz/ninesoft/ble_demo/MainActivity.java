package xyz.ninesoft.ble_demo;

import android.bluetooth.BluetoothDevice;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.SeekBar;

import xyz.ninesoft.ble_demo.databinding.ActivityMainBinding;
import xyz.ninesoft.ble_library.BLE_Client;

public class MainActivity extends AppCompatActivity {
    BLE_Client ble_client;
    private RecyclerViewUitl.MyAdapter adapter;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ble_client = new BLE_Client(device -> {
            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
            });

        }, getApplicationContext());
        ble_client.scanStart();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recuclerView = findViewById(R.id.list_view);
        recuclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewUitl.MyAdapter(
                ble_client.getDeviceList(), device -> {
            Log.i("BLE", "onCreate: " + device.getName() + device.getAddress());
            binding.device.setText(device.getName());
            binding.device.setTag(device);
        });
        recuclerView.setAdapter(adapter);

        binding.connect.setOnClickListener(v -> {
            BluetoothDevice device = (BluetoothDevice) binding.device.getTag();
            ble_client.connect(device.getAddress());
        });

        binding.seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.data.setText(Integer.toString(progress));
                ble_client.sendData(Integer.toString(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
