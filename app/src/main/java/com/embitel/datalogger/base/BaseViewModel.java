package com.embitel.datalogger.base;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;

import com.embitel.datalogger.blemodule.data.BleDevice;

public abstract class BaseViewModel extends ViewModel {


    private final ObservableBoolean mIsLoading = new ObservableBoolean();

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

}
