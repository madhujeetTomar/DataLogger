package com.embitel.datalogger.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.embitel.datalogger.base.BaseViewModel;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SampleGattAttributes;
import com.embitel.datalogger.bleutils.SharedPreferenceConstant;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.model.ResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingViewModel extends BaseViewModel {

    private SettingsRepository settingsRepository = new SettingsRepository();
    private MutableLiveData<String> mData = new MutableLiveData<>();
    private MutableLiveData<String> errorResult = new MutableLiveData<>();
    private BleDevice mBleDevice;
    private MutableLiveData<String> mMinSpeedLimit = new MutableLiveData<>();
    private MutableLiveData<String> mExceedLimit = new MutableLiveData<>();

    /**
     * getConnectedBleDevicefromFragment
     *
     * @param bleDevice
     */
    public void getBleDevice(BleDevice bleDevice) {
        mBleDevice = bleDevice;
    }

    /**
     * retrive value from BLe devices
     *
     * @param characteristics
     */
    public void readData(String characteristics) {
        settingsRepository.readData(new ResponseListener<String>() {
            @Override
            public void success(String s) {
                mData.postValue(s);
            }

            @Override
            public void error(String error) {
                errorResult.postValue(error);
            }
        }, mBleDevice, characteristics);
    }


    /**
     * send value for speed
     *
     * @param speed
     */
    public void sendSpeedValue(String speed) {
       if(!speed.isEmpty()){
        if(Integer.parseInt(speed)>50)
        {
            sendMaxSpeed(speed);
        }
        else if(Integer.parseInt(speed)<30)
        {speed="30";
            sendMinSpeed("30");
        }
SharedPreferencesManager.setPreference(SharedPreferenceConstant.SPEED_VALUE,speed);
        updateSettingsConfiguration(speed, Constants.ACTION_SEND_SPEED_VALUE, SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_3, false);
    }}

    private void sendMinSpeed(String speed) {
        mMinSpeedLimit.postValue(speed);
    }

    public void sendMaxSpeed(String speed) {
        mExceedLimit.postValue(speed);
    }


    /**
     * send data to BLe devices
     *
     * @param value
     * @param action
     * @param characteristics
     * @param b gps & dnd
     */

    private void updateSettingsConfiguration(String value, String action, String characteristics, boolean b) {
        settingsRepository.sendData(new ResponseListener<String>() {
            @Override
            public void success(String s) {
                switch (action) {
                    case Constants.ACTION_SEND_GPS_DATA:
                        SharedPreferencesManager.setPreference(SharedPreferenceConstant.SET_GPS_VALUE, b);
                        mData.postValue(Constants.GPS_DATA_UPDATE_SUCCESSFULLY);
                        break;
                    case Constants.ACTION_SEND_DND_DATA:
                        SharedPreferencesManager.setPreference(SharedPreferenceConstant.SET_DND_VALUE, b);
                        mData.postValue(Constants.DND_DATA_UPDATE_SUCCESSFULLY);
                        break;
                    case Constants.ACTION_SEND_PHONE_NUMBER:
                        mData.postValue(Constants.PHONE_NUMBER_DATA_SUCCESSFULLY);
                        break;
                    case Constants.ACTION_SEND_NAME:
                        mData.postValue(Constants.NAME_DATA_UPDATE_SUCCESSFULLY);
                        break;
                    case Constants.ACTION_SEND_SPEED_VALUE:
                        mData.postValue(Constants.SPEED_DATA_UPDATE_SUCCESSFULLY);
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
     * send value for name
     *
     * @param name
     */
    public void sendNameValue(String name) {
        updateSettingsConfiguration(name, Constants.ACTION_SEND_NAME, SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_4, false);
    }

    /**
     * send value for phone number
     *
     * @param number
     */
    public void sendNumberValue(String number) {
        //updateSettingsConfiguration(number, Constants.ACTION_SEND_PHONE_NUMBER, SampleGattAttributes.CURRENT_TIME, false);
    }

    /**
     * send updated value for DND
     *
     * @param value
     */
    public void dndSwitchCheckedChanged(boolean value) {
        setSettingsValue(value, Constants.ACTION_SEND_DND_DATA, SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_1);
    }

    /**
     * send updated boolean for GPS
     *
     * @param value
     */

    public void gpsSwitchCheckedChanged(boolean value) {
        setSettingsValue(value, Constants.ACTION_SEND_GPS_DATA, SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG_2);
    }

    /**
     * setting the value for GPS and DND switch on change listener
     *
     * @param value
     * @param action
     * @param characteristics
     */
    private void setSettingsValue(boolean value, String action, String characteristics) {
        if (value) {

            updateSettingsConfiguration("1", action, characteristics, value);

        } else {
            updateSettingsConfiguration("0", action, characteristics, value);
        }
    }

    /**
     * success msg
     *
     * @return successResult
     */
    LiveData<String> sendDataSuccessfully() {
        return mData;
    }

    /**
     * error msg
     *
     * @return errorsult
     */
    LiveData<String> getErrorResult() {
        return errorResult;
    }

    /**
     *
     * @return
     */
    LiveData<String> getExceedSpeed() {
        return mExceedLimit;
    }

    /**
     *
     * @return
     */
    LiveData<String> getMinSpeed() {
        return mMinSpeedLimit;
    }

}