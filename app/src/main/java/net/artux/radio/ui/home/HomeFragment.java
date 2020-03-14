package net.artux.radio.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.artux.radio.MediaService;
import net.artux.radio.R;
import net.artux.radio.common.BaseFragmentView;
import net.artux.radio.model.Station;
import net.artux.radio.utils.StationsLoader;

public class HomeFragment extends BaseFragmentView {

    private RecyclerView recyclerView;
    private HomePresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.stationsView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        presenter = new HomePresenter(this);
        presenter.viewIsReady();
    }

    @Override
    public void changeStation(Station station, int order) {
        Intent intent = new Intent(getActivity(), MediaService.class);
        intent.setAction("change_station");
        intent.putExtra("station", new Gson().toJson(station));
        intent.putExtra("order", order);

        Log.d("tag", station.title);
        getActivity().startService(intent);
    }

    @Override
    public void setContent() {
        HomeViewAdapter homeViewAdapter = new HomeViewAdapter(getActivity(), StationsLoader.getStations(), presenter);
        recyclerView.setAdapter(homeViewAdapter);
    }

    @Override
    public void showError() {

    }
}
