package com.embitel.datalogger.extra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.embitel.datalogger.bleutils.BLeConstants;

public class ServiceBroadcastReceiver extends BroadcastReceiver {
    private IServiceConfig iServiceConfig;
    public ServiceBroadcastReceiver(IServiceConfig iServiceConfig) {

    this.iServiceConfig=iServiceConfig;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (BLeConstants.ACTION_GATT_CONNECTED.equals(action)) {

            iServiceConfig.connectionStatus(BLeConstants.CONNECTED);
           } else if (BLeConstants.ACTION_GATT_DISCONNECTED.equals(action)) {
        } else if (BLeConstants.ACTION_GATT_EXISTING_CONNECTING.equals(action)) {
            iServiceConfig.connectionStatus(BLeConstants.CONNECTED);
           } else if (BLeConstants.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
             } else if (BLeConstants.ACTION_DATA_AVAILABLE.equals(action)) {
            }
    }
    }

