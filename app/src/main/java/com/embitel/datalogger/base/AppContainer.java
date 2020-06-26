package com.embitel.datalogger.base;

import com.embitel.datalogger.remote.ApiServices;
import com.embitel.datalogger.remote.RetroClass;
import com.embitel.datalogger.ui.home.HomeRepository;
import com.embitel.datalogger.ui.home.HomeViewModelFactory;

import retrofit2.Retrofit;

public class AppContainer {

    private ApiServices retrofit= RetroClass.getApiServices();

    public HomeRepository homeRepository=new HomeRepository(retrofit);
    public HomeViewModelFactory homeViewModelFactory = new HomeViewModelFactory(homeRepository);






}
