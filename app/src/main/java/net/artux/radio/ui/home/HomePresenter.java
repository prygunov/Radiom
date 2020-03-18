package net.artux.radio.ui.home;

import net.artux.radio.utils.StationsLoader;

class HomePresenter {

    private HomeView view;

    HomePresenter(HomeView view) {
        this.view = view;
    }

    void getStations(){
        view.showStations(StationsLoader.getStations());
    }
}
