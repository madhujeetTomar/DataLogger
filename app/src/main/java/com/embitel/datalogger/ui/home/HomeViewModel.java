package com.embitel.datalogger.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.embitel.datalogger.base.BaseViewModel;
import com.embitel.datalogger.blemodule.data.BleDevice;
import com.embitel.datalogger.bleutils.Constants;
import com.embitel.datalogger.bleutils.SharedPreferenceConstant;
import com.embitel.datalogger.bleutils.SharedPreferencesManager;
import com.embitel.datalogger.model.ResponseListener;
import com.embitel.datalogger.model.WeatherResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class HomeViewModel extends BaseViewModel {

    private HomeRepository homeRepository;
    private MutableLiveData<String> mText;
    private CompositeDisposable disposable;
    private MutableLiveData<WeatherResponse> weatherResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mData =  new MutableLiveData<>();
    private MutableLiveData<String> errorResult =new MutableLiveData<>();
    private BleDevice mBleDevice;


    public HomeViewModel(HomeRepository homeRepository) {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
        this.homeRepository = homeRepository;
        disposable = new CompositeDisposable();
    }

    public void getBleDevice(BleDevice bleDevice) {
        mBleDevice = bleDevice;
    }

    public void fetchData(String location) {
        disposable.add(homeRepository.getWeatherData(location).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<WeatherResponse>() {
                    @Override
                    public void onSuccess(@NonNull WeatherResponse weatherResponse) {
                        weatherResponseMutableLiveData.postValue(weatherResponse);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                }));
    }

    public void updateSettingsConfiguration(String value, String action, String characteristics, boolean b) {
        homeRepository.sendData(new ResponseListener<String>() {
            @Override
            public void success(String s) {
                switch (action) {
                    case Constants.ACTION_SEND_WEATHER_UPDATE:
                       // SharedPreferencesManager.setPreference(SharedPreferenceConstant.SET_GPS_VALUE, b);
                        mData.postValue(Constants.GPS_DATA_UPDATE_SUCCESSFULLY);
                        break;
                    case Constants.ACTION_SEND_BATTERY_LEVEL:
                        //SharedPreferencesManager.setPreference(SharedPreferenceConstant.SET_DND_VALUE, b);
                        mData.postValue(Constants.DND_DATA_UPDATE_SUCCESSFULLY);
                        break;
                   /* case Constants.ACTION_SEND_PHONE_NUMBER:
                        mData.postValue(Constants.PHONE_NUMBER_DATA_SUCCESSFULLY);
                        break;
                   */}
            }

            @Override
            public void error(String error) {
                errorResult.postValue(error);
            }
        }, mBleDevice, value, characteristics);
    }




            LiveData<WeatherResponse> getWeatherData() {
        return weatherResponseMutableLiveData;
    }




    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getData() {
        return mData;
    }

    public LiveData<String> getError() {
    return errorResult;
    }
}