package net.artux.radio;

import android.os.IBinder;

import androidx.annotation.NonNull;

import net.artux.radio.common.MainContract;


public class MainPresenter implements MainContract.Presenter {

    @NonNull
    private final MainContract.View view;

    private static String TAG = "Presenter";

    MainPresenter(MainContract.View view){
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
    public void onServiceConnect(IBinder service) {
        view.registerCallback(((MediaService.MediaServiceBinder) service).getMediaSessionToken());
    }

}
