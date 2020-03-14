package net.artux.radio.ui.genres;

import net.artux.radio.common.BaseFragmentView;
import net.artux.radio.common.BasePresenter;
import net.artux.radio.model.Station;

public class GenresPresenter extends BasePresenter {

    private BaseFragmentView view;

    GenresPresenter(GenresFragment view){
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

    }
}
