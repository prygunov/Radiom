package net.artux.radio.common;

import android.support.v4.media.session.MediaSessionCompat;

import net.artux.radio.model.Station;

import java.util.List;

public interface MainContract {

    interface View{
        void registerCallback(MediaSessionCompat.Token sessionToken);
    }

    interface Presenter{
        void updateStatus(boolean playing);
        void onMedia();
        void detachView();
        void destroy();
    }

    interface Repository{
        static List<Station> getStations(){
            return null;
        }
    }
}
