package com.embitel.datalogger.blemodule.callback;

import com.embitel.datalogger.blemodule.data.BleDevice;

import java.util.List;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }
}
