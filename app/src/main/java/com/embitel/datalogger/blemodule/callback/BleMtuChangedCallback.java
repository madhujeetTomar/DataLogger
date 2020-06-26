package com.embitel.datalogger.blemodule.callback;


import com.embitel.datalogger.blemodule.exception.BleException;

public abstract class BleMtuChangedCallback extends BleBaseCallback {

    public abstract void onSetMTUFailure(BleException exception);

    public abstract void onMtuChanged(int mtu);

}
