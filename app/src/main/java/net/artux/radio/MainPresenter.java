package net.artux.radio;

import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.NonNull;

import net.artux.radio.common.BasePresenter;
import net.artux.radio.common.MainContract;
import net.artux.radio.model.Station;


public class MainPresenter extends BasePresenter implements MainContract.Presenter {

    @NonNull
    private final MainContract.View view;

    private static String TAG = "Presenter";

    public MainPresenter(MainContract.View view){
        this.view = view;
    }

    @Override
    public void onMedia() {

    }

    @Override
    public void detachView() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void updateStatus(boolean playing) {

    }

    @Override
    public void viewIsReady() {

    }

    @Override
    public void showError() {

    }

    @Override
    public void onServiceConnect(IBinder service) {
        view.registerCallback(((MediaService.MediaServiceBinder) service).getMediaSessionToken());
    }

    @Override
    public void changeStation(Station station, int order) {

    }

}
