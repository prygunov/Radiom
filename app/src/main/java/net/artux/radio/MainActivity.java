package net.artux.radio;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import net.artux.radio.common.MainContract;
import net.artux.radio.model.Stream;
import net.artux.radio.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    static String TAG = "MainActivity";

    ImageButton imageButton;
    TextView titleView;
    TextView artistView;

    MainPresenter presenter;
    MediaControllerCompat mediaController;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragmentTransaction.replace(R.id.container, new HomeFragment());
                fragmentTransaction.commit();
                return true;
            case R.id.navigation_my_stations:

                return true;
            case R.id.navigation_genres:

                return true;
        }

        return false;
    };

    ServiceConnection serviceConnection =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            presenter.onServiceConnect(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaController = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = findViewById(R.id.mediaButton);
        titleView = findViewById(R.id.titleView);
        artistView = findViewById(R.id.artistView);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        presenter = new MainPresenter(this);
        presenter.viewIsReady();

        bind();
    }

    public void onMedia(View view) {
        if(mediaController.getPlaybackState() == null){
            mediaController.getTransportControls().play();
        }else if(mediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING){
            mediaController.getTransportControls().pause();
        } else {
            mediaController.getTransportControls().play();
        }
    }

    public void updateStatus(PlaybackStateCompat state) {
        if (state == null)
            return;
        boolean playing =
                state.getState() == PlaybackStateCompat.STATE_PLAYING;
        if (playing)
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_pause));
        else
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_play));
        if (state.getExtras() != null) {
            Stream stream = (Stream) state.getExtras().getSerializable("stream");
            if (stream != null && stream.mediaData != null) {
                artistView.setText(stream.mediaData.artist);
                titleView.setText(stream.mediaData.title);
            }
        }


    }

    @Override
    public void registerCallback(MediaSessionCompat.Token sessionToken) {
        try {
            mediaController = new MediaControllerCompat(
                    getApplicationContext(), sessionToken);
            mediaController.registerCallback(
                    new MediaControllerCompat.Callback() {
                        @Override
                        public void onPlaybackStateChanged(PlaybackStateCompat state) {
                            updateStatus(state);
                        }
                    });
            if(mediaController.getPlaybackState()!=null){
                updateStatus(mediaController.getPlaybackState());
            }
        } catch (RemoteException e) {
            mediaController = null;
            e.fillInStackTrace().printStackTrace();
        }
    }

    public void bind(){
        bindService(new Intent(getApplicationContext(), MediaService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}