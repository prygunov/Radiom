package net.artux.radio.ui.home;

import android.os.IBinder;

import net.artux.radio.common.BaseFragmentView;
import net.artux.radio.common.BasePresenter;
import net.artux.radio.model.Station;
import net.artux.radio.utils.StationsLoader;

public class HomePresenter extends BasePresenter {

    BaseFragmentView view;


    public HomePresenter(BaseFragmentView view) {
        this.view = view;
    }

    @Override
    public void viewIsReady() {
        view.setList();
    }

    @Override
    public void showError() {

    }

    @Override
    public void onServiceConnect(IBinder service) {

    }

    @Override
    public void changeStation(Station station, int order) {
        view.changeStation(station, order);
    }
}
