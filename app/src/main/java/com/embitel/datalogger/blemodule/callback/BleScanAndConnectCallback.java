package com.embitel.datalogger.blemodule.callback;


import com.embitel.datalogger.blemodule.data.BleDevice;

public abstract class BleScanAndConnectCallback extends BleGattCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(BleDevice scanResult);

    public void onLeScan(BleDevice bleDevice) {
    }

}
