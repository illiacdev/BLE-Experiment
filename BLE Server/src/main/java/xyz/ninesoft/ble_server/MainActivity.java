package xyz.ninesoft.ble_server;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import xyz.ninesoft.ble_library.BLE_Server;
import xyz.ninesoft.ble_server.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BLE_Server ble_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ble_server = new BLE_Server(data -> {
            Log.i("BLE", "onCreate: " + data);
            runOnUiThread(() -> {

                binding.textView.setText(data);
                binding.seekBar.setProgress(Integer.valueOf(data));
            });


        }, getApplicationContext());
        binding.startAdvertising.setOnClickListener(v -> {

            ble_server.startAdvertise();
        });

        binding.stopAdvertising.setOnClickListener(v -> {
            ble_server.stopAdvertise();
            Log.i("BLE", "stopAdvertising: ");

        });
    }
}
