package net.artux.radio.ui.home;

import android.view.View;

import net.artux.radio.common.BaseView;
import net.artux.radio.model.Station;

import java.util.List;

public interface HomeView extends BaseView {

    void showStations(List<Station> stations);

    void openStation(Station station, View view, String sharedName);

}
