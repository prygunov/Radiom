package net.artux.radio;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.artux.radio.common.MainContract;
import net.artux.radio.ui.genres.GenresFragment;
import net.artux.radio.ui.home.HomeFragment;

public class MainActivity extends AppCompatActivity implements MainContract.View, View.OnClickListener {

    static String TAG = "MainActivity";

    ImageButton playingButton0;
    ImageButton playingButton1;
    TextView titleView;
    TextView artistView;
    View mediaBar;
    View closeBtn;

    MainPresenter presenter;
    MediaControllerCompat mediaController;
    Fragment current;

    BottomSheetBehavior bottomSheetBehavior;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                current = new HomeFragment();
                fragmentTransaction.replace(R.id.container, current);
                fragmentTransaction.commit();
                return true;
            case R.id.navigation_my_stations:

                return true;
            case R.id.navigation_genres:
                current = new GenresFragment();
                fragmentTransaction.replace(R.id.container, current);
                fragmentTransaction.commit();
                return true;
        }

        return false;
    };

    public void replaceFragment(Fragment fragment, View view, String sharedName){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.default_transition));
            fragment.setEnterTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition));
        }

        fragmentTransaction
                .replace(R.id.container, fragment)
                .addSharedElement(view, sharedName)
                .addToBackStack(null)
                .commit();
    }

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

        playingButton0 = findViewById(R.id.mediaButton0);
        playingButton1 = findViewById(R.id.mediaButton1);
        titleView = findViewById(R.id.titleView);
        artistView = findViewById(R.id.artistView);
        mediaBar = findViewById(R.id.mediaBar);
        closeBtn = findViewById(R.id.closeBtn);

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        mediaBar.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        presenter = new MainPresenter(this);

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        navView.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            bottomSheetBehavior.setPeekHeight(navView.getHeight() + llBottomSheet.getChildAt(0).getHeight());
            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0,0,0,navView.getHeight() + llBottomSheet.getChildAt(0).getHeight());
            findViewById(R.id.container).setLayoutParams(lp);
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    navView.setVisibility(View.GONE);
                    mediaBar.setVisibility(View.INVISIBLE);
                    closeBtn.setVisibility(View.VISIBLE);
                }
                if(newState == BottomSheetBehavior.STATE_COLLAPSED)
                    closeBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                navView.setVisibility(View.VISIBLE);
                mediaBar.setVisibility(View.VISIBLE);
                navView.setAlpha(1-slideOffset);
                mediaBar.setAlpha(1-slideOffset);
                closeBtn.setAlpha(slideOffset);
            }
        });

        bindService();
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

    public void updateStatus(PlaybackStateCompat state, MediaMetadataCompat metadataCompat) {
        if (state == null)
            return;
        boolean playing =
                state.getState() == PlaybackStateCompat.STATE_PLAYING;
        if (playing) {
            playingButton0.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            playingButton1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
        } else {
            playingButton0.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            playingButton1.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
        }
        if (metadataCompat != null) {
            artistView.setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
            titleView.setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
            ((TextView) findViewById(R.id.artistLabel)).setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_ARTIST));
            ((TextView) findViewById(R.id.titleLabel)).setText(metadataCompat.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
            ((ImageView) findViewById(R.id.streamLogo)).setImageBitmap(metadataCompat.getBitmap(MediaMetadataCompat.METADATA_KEY_ART));
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
                            updateStatus(state, mediaController.getMetadata());
                        }

                        @Override
                        public void onSessionReady() {
                            super.onSessionReady();
                            updateStatus(mediaController.getPlaybackState(), mediaController.getMetadata());
                        }
                    });
        } catch (RemoteException e) {
            mediaController = null;
            e.fillInStackTrace().printStackTrace();
        }
    }

    public void bindService(){
        bindService(new Intent(getApplicationContext(), MediaService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mediaBar:
                if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.closeBtn:
                if(bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
        }
    }
}