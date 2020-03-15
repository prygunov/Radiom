package net.artux.radio.ui.station;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.artux.radio.MainActivity;
import net.artux.radio.MediaService;
import net.artux.radio.R;
import net.artux.radio.model.Station;
import net.artux.radio.model.Stream;

public class StationFragment extends Fragment implements StationView, StreamsAdapter.OnStreamClick {

    private RecyclerView streamsView;
    private StreamsAdapter streamsAdapter;
    private CollapsingToolbarLayout ctoolbar;
    private Toolbar toolbar;

    private Station station;

    public static StationFragment newInstance(Station station) {
        Bundle args = new Bundle();

        args.putString("st", new Gson().toJson(station));

        StationFragment fragment = new StationFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station, container, false);
        streamsView = view.findViewById(R.id.streamsView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        streamsView.setLayoutManager(linearLayoutManager);

        if (getArguments()!=null){
            station = new Gson().fromJson(getArguments().getString("st"), Station.class);
            streamsAdapter = new StreamsAdapter(this, station.streams);

            streamsView.setAdapter(streamsAdapter);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        toolbar = view.findViewById(R.id.toolbar);

        ctoolbar = view.findViewById(R.id.toolbar_layout);
        ctoolbar.setTransitionName("0");
        if (getActivity()!=null)
            ((MainActivity)getActivity()).setSupportActionBar(toolbar);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments()!=null){
            Log.d("station", station.title + station.desc);
            ctoolbar.setTitle(station.title);
            Picasso.get().load(station.imageUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    ctoolbar.setBackground(new BitmapDrawable(getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            toolbar.setSubtitle(station.desc);

        }
    }

    @Override
    public void setStream(int pos) {
        Intent intent = new Intent(getActivity(), MediaService.class);
        intent.setAction("change_station");
        intent.putExtra("station", new Gson().toJson(station));
        intent.putExtra("order", pos);

        Log.d("tag", station.title);
        if (getActivity()!=null)
            getActivity().startService(intent);
    }

    @Override
    public void addToFavorite(Stream stream) {

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
}
