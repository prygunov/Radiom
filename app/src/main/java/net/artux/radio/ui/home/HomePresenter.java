package net.artux.radio.ui.home;

import net.artux.radio.common.BaseFragmentView;
import net.artux.radio.common.BasePresenter;
import net.artux.radio.model.Station;

public class HomePresenter extends BasePresenter {

    private BaseFragmentView view;

    HomePresenter(BaseFragmentView view) {
        this.view = view;
    }

    @Override
    public void viewIsReady() {
        view.setContent();
    }

    @Override
    public void showError() {

    }

    @Override
    public void changeStation(Station station, int order) {
        view.changeStation(station, order);
    }
}
