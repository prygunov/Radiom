package net.artux.radio.common;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import net.artux.radio.MediaService;
import net.artux.radio.model.Station;

public abstract class BasePresenter {

    public abstract void viewIsReady();
    public abstract void showError();
    public abstract void onServiceConnect(IBinder service);
    public abstract void changeStation(Station station, int order);

}
