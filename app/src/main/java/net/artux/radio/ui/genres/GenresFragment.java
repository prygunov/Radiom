package net.artux.radio.ui.genres;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import net.artux.radio.R;
import net.artux.radio.common.BaseFragmentView;
import net.artux.radio.model.Station;

public class GenresFragment extends BaseFragmentView {

    private RecyclerView genresView;
    private GenresPresenter genresPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genres, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        genresView = view.findViewById(R.id.genresView);
        genresPresenter = new GenresPresenter(this);

        genresPresenter.viewIsReady();
    }

    @Override
    public void changeStation(Station station, int order) {

    }

    @Override
    public void setContent() {

    }

    @Override
    public void showError() {

    }
}
