package xyz.ninesoft.ble_demo;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by illiacDev on 2017-12-07.
 */

public class RecyclerViewUitl {
    public interface IOUT{
        void onClick(BluetoothDevice device);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView itemView;
        public ViewHolder(View itemView) {
            super(itemView);

            this.itemView = (TextView) itemView;
            this.itemView.setTextSize(50);
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        final private List<BluetoothDevice> mDeviceList;
        final IOUT iout;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iout.onClick((BluetoothDevice) v.getTag());
            }
        };

        MyAdapter(List<BluetoothDevice> mDeviceList,
                  IOUT iout) {this.mDeviceList = mDeviceList;
            this.iout = iout;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            TextView view = new TextView(parent.getContext());
            view.setOnClickListener(listener);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setText(mDeviceList.get(position).getName());

            holder.itemView.setTag(mDeviceList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDeviceList.size();
        }
    }
}
