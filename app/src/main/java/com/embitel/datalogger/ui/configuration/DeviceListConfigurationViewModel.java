package com.embitel.datalogger.ui.configuration;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.embitel.datalogger.R;
import com.embitel.datalogger.base.BaseViewModel;
import com.embitel.datalogger.blemodule.BleManager;
import com.embitel.datalogger.blemodule.callback.BleGattCallback;
import com.embitel.datalogger.blemodule.callback.BleScanCallback;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.blemodule.exception.BleException;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SharedPreferenceConstant;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.bleutils.rx.SchedulerProvider;
import com.embitel.datalogger.model.ResponseListener;
import com.embitel.datalogger.ui.settings.SettingsRepository;
import com.google.gson.Gson;

import java.util.List;

public class DeviceListConfigurationViewModel extends BaseViewModel {
    private SettingsRepository settingsRepository = new SettingsRepository();
    private MutableLiveData<BleDevice> mData = new MutableLiveData<>();
    private MutableLiveData<String> btnStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> mMessage = new MutableLiveData<>();
    private MutableLiveData<BleDevice> mConfiguredBleDevice = new MutableLiveData<>();
    private MutableLiveData<String> mTimeSyncData = new MutableLiveData<>();
    private MutableLiveData<String> errorResult = new MutableLiveData<>();


    void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                isLoading.postValue(true);
                btnStatus.postValue("Stop Scan");
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if(bleDevice!=null)
                {

                if (!SharedPreferencesManager.getBLeDevice().isEmpty()) {
                    BleDevice mBleDevice = new Gson().fromJson(SharedPreferencesManager.getBLeDevice(), BleDevice.class);
                    Log.d("vALUE", "onScanning: "+mBleDevice);
                    if (mBleDevice.getMac().equals(bleDevice.getMac())) {
                        bleDevice.setConfigured(true);
                        mConfiguredBleDevice.postValue(bleDevice);
                    } else {
                        bleDevice.setConfigured(false);
                    }
                }
                mData.postValue(bleDevice);
            }

            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                isLoading.postValue(false);
                btnStatus.postValue("Start Scan");
            }
        });
    }

    LiveData<String> btnStatus() {
        return btnStatus;
    }

    LiveData<BleDevice> scBLeDevices() {
        return mData;
    }


    void connect(BleDevice device) {
        BleManager.getInstance().connect(device, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                isLoading.postValue(true);
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                isLoading.postValue(false);
                mMessage.postValue("Connection Failed!!!");
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                isLoading.postValue(false);
                mMessage.postValue("Connection established successfully");
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                isLoading.postValue(false);
                mMessage.postValue("Connection Disconnect!!!");
            }
        });
    }

    LiveData<Boolean> setIsLoading() {
        return isLoading;
    }

    LiveData<String> getResponse() {
        return mMessage;
    }
    LiveData<BleDevice> getConfiguredDevice() {
        return mConfiguredBleDevice;
    }

    public void updateSettingsConfiguration(String value, String action, String characteristics, BleDevice mBleDevice) {
        settingsRepository.sendData(new ResponseListener<String>() {
            @Override
            public void success(String s) {
                switch (action) {
                    case Constants.ACTION_SEND_TIME:
                        mTimeSyncData.postValue(Constants.TIME_UPDATE_SUCCESSFULLY);
                        break;
                 }
            }

            @Override
            public void error(String error) {
                errorResult.postValue(error);
            }
        }, mBleDevice, value, characteristics);
    }
    /**
     * success msg
     *
     * @return successResult
     */
    LiveData<String> sendDataSuccessfully() {
        return mTimeSyncData;
    }

    /**
     * error msg
     *
     * @return errorsult
     */
    LiveData<String> getErrorResult() {
        return errorResult;
    }


}
