package com.embitel.datalogger.blemodule.callback;


import com.embitel.datalogger.blemodule.data.BleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(BleDevice bleDevice);

}
