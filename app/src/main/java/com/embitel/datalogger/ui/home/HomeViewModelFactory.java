package com.embitel.datalogger.ui.home;

import com.embitel.datalogger.base.Factory;

public class HomeViewModelFactory implements Factory<HomeViewModel> {
    private HomeRepository homeRepository;
    public HomeViewModelFactory(HomeRepository homeRepository)
    {
        this.homeRepository=homeRepository;
    }
    @Override
    public HomeViewModel create() {
        return new HomeViewModel(homeRepository);
    }
}
