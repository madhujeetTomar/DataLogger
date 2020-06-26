package com.embitel.datalogger.ui.fota;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FOTAViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FOTAViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}