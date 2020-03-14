package net.artux.radio.common;

import androidx.fragment.app.Fragment;

import net.artux.radio.model.Station;
import net.artux.radio.ui.home.HomeViewAdapter;

public abstract class BaseFragmentView extends Fragment {
    //below bad code
    public abstract void changeStation(Station station, int order);
    public abstract void setContent();
    public abstract void showError();

}
