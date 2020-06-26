package com.embitel.datalogger.model;

import com.embitel.datalogger.blemodule.data.BleDevice;

public class DeviceDetail {
    private BleDevice bleDevice;

    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }
}
