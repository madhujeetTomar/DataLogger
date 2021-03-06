package com.embitel.datalogger.extra;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.util.List;
import java.util.Set;

public class RxBleAdapterWrapper {
    private static final String TAG = "RxBleAdapterWrapper";

    private final BluetoothAdapter bluetoothAdapter;

    public RxBleAdapterWrapper(@Nullable BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public BluetoothDevice getRemoteDevice(String macAddress) {
        return bluetoothAdapter.getRemoteDevice(macAddress);
    }

    public boolean hasBluetoothAdapter() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    public boolean startLegacyLeScan(BluetoothAdapter.LeScanCallback leScanCallback) {
        return bluetoothAdapter.startLeScan(leScanCallback);
    }

    public void stopLegacyLeScan(BluetoothAdapter.LeScanCallback leScanCallback) {
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    @TargetApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    public void startLeScan(List<ScanFilter> scanFilters, ScanSettings scanSettings, ScanCallback scanCallback) {
        bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, scanCallback);
    }

    @RequiresApi(26 /* Build.VERSION_CODES.O */)
    public int startLeScan(List<ScanFilter> scanFilters, ScanSettings scanSettings, PendingIntent callbackIntent) {
        return bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettings, callbackIntent);
    }

    @RequiresApi(26 /* Build.VERSION_CODES.O */)
    public void stopLeScan(PendingIntent callbackIntent) {
        bluetoothAdapter.getBluetoothLeScanner().stopScan(callbackIntent);
    }

    @TargetApi(21 /* Build.VERSION_CODES.LOLLIPOP */)
    public void stopLeScan(ScanCallback scanCallback) {
        if (!bluetoothAdapter.isEnabled()) {
            // this situation seems to be a problem since API 29

            Log.d(TAG, "stopLeScan: "+ "BluetoothAdapter is disabled, calling BluetoothLeScanner.stopScan(ScanCallback) may cause IllegalStateException"
            );
         
            // if stopping the scan is not possible due to BluetoothAdapter turned off then it is probably stopped anyway
            return;
        }
        final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner == null) {
         
            // if stopping the scan is not possible due to BluetoothLeScanner not accessible then it is probably stopped anyway
            // this should not happen since the check for BluetoothAdapter.isEnabled() has been added above. This situation was only
            // observed when the adapter was disabled
            return;
        }
        bluetoothLeScanner.stopScan(scanCallback);
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }
}
