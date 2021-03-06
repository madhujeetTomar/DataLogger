package com.embitel.datalogger.blemodule.callback;

import com.embitel.datalogger.blemodule.exception.BleException;

public abstract class BleReadCallback extends BleBaseCallback {

    public abstract void onReadSuccess(byte[] data);

    public abstract void onReadFailure(BleException exception);

}
