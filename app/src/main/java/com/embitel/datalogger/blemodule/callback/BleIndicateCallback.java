package com.embitel.datalogger.blemodule.callback;


import com.embitel.datalogger.blemodule.exception.BleException;

public abstract class BleIndicateCallback extends BleBaseCallback{

    public abstract void onIndicateSuccess();

    public abstract void onIndicateFailure(BleException exception);

    public abstract void onCharacteristicChanged(byte[] data);
}
