package com.embitel.datalogger.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.embitel.datalogger.R;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.ui.configuration.DeviceListConfigurationScreen;

import java.util.ArrayList;
import java.util.List;

public class DeviceLeAdapter extends RecyclerView.Adapter<DeviceLeAdapter.ViewHolder> {
    //private MyListData[] listdata;
    List<BleDevice> deviceData;
    onDevicesItemClick onDevicesItemClick;
    private Context context;

    public DeviceLeAdapter(Context context) {
        this.context = context;
        deviceData = new ArrayList<>();
    }

// RecyclerView recyclerView;

    @Override
    public DeviceLeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_scan_devices, parent, false);
        DeviceLeAdapter.ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceLeAdapter.ViewHolder holder, final int position) {

        if (holder != null && !deviceData.isEmpty()) {
            if (deviceData.get(position).getName() != null) {
                if(deviceData.get(position).isConfigured())

                {
                    holder.txName.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
                    holder.txtStatus.setText(R.string.configured);
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
                    holder.imgBle.setBackground(ContextCompat.getDrawable(context,R.drawable.conf_ble));
holder.txtStatus.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.txName.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
                    holder.imgBle.setBackground(ContextCompat.getDrawable(context,R.drawable.not_conf_ble));
                    holder.txtStatus.setVisibility(View.GONE);

                }


                holder.txName.setText(deviceData.get(position).getName());
            } else {
                holder.txName.setText("Unknown Device");
            }

            holder.txAddress.setText(deviceData.get(position).getMac());

            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDevicesItemClick.onDevicesItemClick(deviceData.get(position));
                }
            });
        }
    }

    public void setOnDevicesItemClick(onDevicesItemClick onDevicesItemClick) {
        this.onDevicesItemClick = onDevicesItemClick;
    }

    @Override
    public int getItemCount() {
        return deviceData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txName;
        public TextView txAddress, txtStatus;
        public LinearLayout linearLayout;
        private ImageView imgBle;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txName = (TextView) itemView.findViewById(R.id.tx_name);
            this.txAddress = (TextView) itemView.findViewById(R.id.tx_address);
            this.imgBle=itemView.findViewById(R.id.img_ble);
            this.txtStatus = (TextView) itemView.findViewById(R.id.txt_configured_status);
            this.linearLayout = itemView.findViewById(R.id.ll_device);
        }
    }

    public void addDevice(BleDevice device) {
        if (!deviceData.contains(device)) {
            deviceData.add(device);
        }
    }

    public interface onDevicesItemClick {
        void onDevicesItemClick(BleDevice device);
    }
}