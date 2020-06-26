package com.embitel.datalogger.blemodule.callback;


import com.embitel.datalogger.blemodule.exception.BleException;

public abstract class BleRssiCallback extends BleBaseCallback{

    public abstract void onRssiFailure(BleException exception);

    public abstract void onRssiSuccess(int rssi);

}