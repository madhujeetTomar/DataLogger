package com.embitel.datalogger.ui.settings;

import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.callback.BleReadCallback;
import com.embitel.datalogger.blemodule.callback.BleWriteCallback;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.blemodule.exception.BleException;
import com.embitel.datalogger.blemodule.utils.HexUtil;
import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.model.ResponseListener;

public class SettingsRepository {
    public void sendData(ResponseListener<String> response, BleDevice bleDevice, String value, String characteristics) {

        BleManager.getInstance().write(bleDevice, SampleGattAttributes.TIME_SERVICE,
                characteristics, HexUtil.stringToByte(value), new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        response.success(HexUtil.encodeHexStr(justWrite));
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        response.error(exception.getDescription());
                    }
                });
    }

    public void readData(ResponseListener<String> response, BleDevice bleDevice, String characteristics) {

        BleManager.getInstance().read(bleDevice, SampleGattAttributes.BLE_SERVICE_2,
                SampleGattAttributes.READ_TIME, new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        String resp = new String(data);
                        response.success(resp);
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        response.error(exception.getDescription());
                    }
                });
    }


}
