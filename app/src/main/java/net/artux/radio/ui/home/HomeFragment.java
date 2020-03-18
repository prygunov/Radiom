package net.artux.radio.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import net.artux.radio.MainActivity;
import net.artux.radio.R;
import net.artux.radio.model.Station;
import net.artux.radio.ui.station.StationFragment;
import net.artux.radio.utils.StationsLoader;

import java.util.List;

public class HomeFragment extends Fragment implements HomeView, HomeViewAdapter.OnItemClick {

    private RecyclerView recyclerView;
    private HomeViewAdapter homeViewAdapter;
    private HomePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.stationsView);
        homeViewAdapter = new HomeViewAdapter(this, StationsLoader.getStations());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(homeViewAdapter);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        presenter = new HomePresenter(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter.getStations();
    }

    @Override
    public void showRefresh() {

    }

    @Override
    public void hideRefresh() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void showStations(List<Station> stations) {
        homeViewAdapter.addData(stations, true);
        recyclerView.setAdapter(homeViewAdapter);
    }

    @Override
    public void openStation(Station station, View view, String sharedName) {
        setSharedElementReturnTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.default_transition));
        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.no_transition));
        if(getActivity()!=null)
            ((MainActivity)getActivity()).replaceFragment(StationFragment.newInstance(station), view, sharedName);
    }

    @Override
    public void onClick(Station station, View view, String sharedName) {
        openStation(station, view, sharedName);
    }
}
