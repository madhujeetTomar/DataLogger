package com.embitel.datalogger.ui.home;

import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.callback.BleWriteCallback;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.blemodule.exception.BleException;
import com.embitel.datalogger.blemodule.utils.HexUtil;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.model.ResponseListener;
import com.embitel.datalogger.model.WeatherResponse;
import com.embitel.datalogger.remote.ApiServices;

import io.reactivex.Single;


public class HomeRepository {
    private final ApiServices retrofit;

    public HomeRepository(ApiServices retrofit) {
        this.retrofit = retrofit;
    }

    public Single<WeatherResponse> getWeatherData(String location) {
        return retrofit.getWeatherUpdate(Constants.APP_ID,location,"metric");
    }
    public void sendData(ResponseListener<String> response, BleDevice bleDevice, String value, String characteristics) {

        BleManager.getInstance().write(bleDevice, SampleGattAttributes.BLE_SERVICE_1,
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
}
