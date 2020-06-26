package com.embitel.datalogger.extra;

import android.content.ComponentName;
import android.os.IBinder;
import android.util.Log;

import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.extra.BluetoothLeService;

public class ServiceConfig {
    private static final String TAG = "ServiceConfig";
    State state;

    private BluetoothLeService mBluetoothLeService;

    public ServiceConfig(State state) {

    this.state=state;
    }


    public final android.content.ServiceConnection mServiceConnection = new android.content.ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
            }
            Log.d(TAG, "onServiceConnected: "+mBluetoothLeService.connect(SharedPreferencesManager.getCategoryid()));// mBluetoothLeService.connect(SharedPreferencesManager.getCategoryid());

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    void connect(String mDeviceAddress)
    {
        mBluetoothLeService.connect(mDeviceAddress);
    }

    int isConnected(String mDeviceAddress)
    {
       return mBluetoothLeService.isConnected();
    }

    interface State
    {
        void updatedState(String value);
    }
}
