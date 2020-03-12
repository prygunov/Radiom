package net.artux.radio;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.artux.radio.model.ICYMetadata;
import net.artux.radio.model.Qualities;
import net.artux.radio.model.Station;
import net.artux.radio.model.Stream;

import java.util.Iterator;
import java.util.List;


public class MediaService extends MediaBrowserServiceCompat implements ExoPlayer.EventListener {

    private static String TAG = "MediaService";

    static int NOTIFICATION_ID = 1;

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            callback.onPlay();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            callback.onPause();
                            break;
                        default:
                            callback.onPause();
                            break;
                    }
                }
            };

    private AudioManager audioManager;

    private MediaSessionCompat mSession;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_NEXT = "action_next";

    final MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

    final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
            .setActions(
                    PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_STOP
                            | PlaybackStateCompat.ACTION_PAUSE
                            | PlaybackStateCompat.ACTION_PLAY_PAUSE
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                            | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);


    MediaSessionCompat.Callback callback = new MediaSessionCompat.Callback() {

        private SimpleExoPlayer exoPlayer;
        private Station currentStation;
        private Stream currentStream;
        private int orderStream;

        void setStream(int orderStream) {
            this.orderStream = orderStream;
            currentStream = currentStation.streams.get(orderStream);
            String imageUrl;
            if (currentStream.imageUrl == null || currentStream.imageUrl.equals("")) {
                imageUrl = currentStation.imageUrl;
            } else {
                imageUrl = currentStream.imageUrl;
            }

            Picasso.get().load(imageUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap);
                    mSession.setMetadata(metadataBuilder.build());
                    Bundle extras = new Bundle();
                    extras.putSerializable("stream", currentStream);
                    mSession.setPlaybackState(
                            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1)
                                    .setExtras(extras)
                                    .build());
                    refreshNotificationAndForegroundStatus(mSession.getController().getPlaybackState().getState());
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    MediaMetadataCompat metadata = metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, ((BitmapDrawable) placeHolderDrawable).getBitmap())
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Загрузка названия..")
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentStream.title)
                            .build();
                    mSession.setMetadata(metadata);

                    Bundle extras = new Bundle();
                    extras.putSerializable("stream", currentStream);
                    mSession.setPlaybackState(
                            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1)
                                    .setExtras(extras)
                                    .build());
                    refreshNotificationAndForegroundStatus(mSession.getController().getPlaybackState().getState());
                }
            });
            onPlay();
        }

        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            switch (command) {
                case "change_station":
                    currentStation = new Gson().fromJson(extras.getString("station"), Station.class);
                    setStream(extras.getInt("order", 0));
                    Log.d("tag", currentStation.title + currentStream.title);
                    break;
            }
        }

        @Override
        public void onPlay() {
            super.onPlay();
            Log.d(TAG, "onPlay");
            startService(new Intent(getApplicationContext(), MediaService.class));

            int audioFocusResult = audioManager.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                return;

            if (exoPlayer != null)
                exoPlayer.release();
            Iterator<String> iterator = currentStream.types.keySet().iterator();
            String q = Qualities.b320;
            while (iterator.hasNext()) {
                q = iterator.next();
            }
            exoPlayer = preparedExoPlayerFromURL(currentStream.types.get(q));

            exoPlayer.addMetadataOutput(new MetadataOutput() {

                private MediaMetadataCompat parseAndGetMetadata(Metadata data) {
                    String type = data.get(0).toString().substring(0,
                            data.get(0).toString().indexOf(":"));
                    Stream.MediaData mediaData = new Stream.MediaData();
                    switch (type) {
                        case "ICY":
                            String icy = data.get(0).toString().substring(data.get(0).toString().indexOf(":") + 1);
                            ICYMetadata icyMetadata = ICYMetadata.fromString(icy);
                            if (!icyMetadata.isEmpty()) {
                                mediaData.artist = icyMetadata.getArtist();
                                mediaData.title = icyMetadata.getTitle();
                            }
                    }
                    if (!mediaData.isEmpty()) {
                        metadataBuilder
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mediaData.title)
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentStream.title)
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mediaData.artist);
                    } else {
                        metadataBuilder
                                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentStream.title)
                                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "")
                                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentStation.title);
                    }

                    return metadataBuilder.build();
                }

                @Override
                public void onMetadata(Metadata metadata) {
                    MediaMetadataCompat metadataCompat = parseAndGetMetadata(metadata);
                    mSession.setMetadata(metadataCompat);

                    Bundle extras = new Bundle();
                    currentStream.mediaData = new Stream.MediaData();
                    currentStream.mediaData.artist = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    currentStream.mediaData.title = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                    extras.putSerializable("stream", currentStream);
                    mSession.setPlaybackState(
                            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1)
                                    .setExtras(extras)
                                    .build());

                    refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_PLAYING);
                }
            });

            mSession.setActive(true);
            registerReceiver(
                    becomingNoisyReceiver,
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

            refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_PLAYING);
        }

        @Override
        public void onPause() {
            super.onPause();
            Log.d(TAG, "onPause");

            if (exoPlayer != null) {
                exoPlayer.release();
            }
            mSession.setPlaybackState(
                    stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            refreshNotificationAndForegroundStatus(PlaybackStateCompat.STATE_PAUSED);
            if (becomingNoisyReceiver != null)
                unregisterReceiver(becomingNoisyReceiver);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            orderStream--;
            if (orderStream < 0) orderStream = currentStation.streams.size() - 1;
            setStream(orderStream);
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            orderStream++;
            if (orderStream > currentStation.streams.size() - 1)
                orderStream = 0;
            setStream(orderStream);
        }

        @Override
        public void onStop() {
            super.onStop();
            Log.d(TAG, "onStop");
            if (exoPlayer != null) {
                exoPlayer.stop();
                exoPlayer.release();
            }
            audioManager.abandonAudioFocus(audioFocusChangeListener);
            mSession.setActive(false);
            mSession.setPlaybackState(
                    stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED,
                            PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1).build());
            if (becomingNoisyReceiver != null)
                unregisterReceiver(becomingNoisyReceiver);
            stopSelf();
        }

        final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
                    callback.onPause();
            }
        };

    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSession = new MediaSessionCompat(this, "radiom-service");
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setCallback(callback);

        Context appContext = getApplicationContext();

        Intent activityIntent = new Intent(appContext, MainActivity.class);
        mSession.setSessionActivity(
                PendingIntent.getActivity(appContext, 0, activityIntent, 0));
        Intent mediaButtonIntent = new Intent(
                Intent.ACTION_MEDIA_BUTTON, null, appContext, MediaButtonReceiver.class);
        mSession.setMediaButtonReceiver(
                PendingIntent.getBroadcast(appContext, 0, mediaButtonIntent, 0));
    }

    public void onDestroy() {
        super.onDestroy();
        callback.onStop();
        mSession.release();
    }


    public int onStartCommand(Intent intent, final int flags, final int startId) {
        Log.d(TAG, "onStart");
        if (intent.getAction() != null)
            switch (intent.getAction()) {
                case Actions.CHANGE_STATION:
                    callback.onCommand("change_station", intent.getExtras(), null);

                    break;
            }
        MediaButtonReceiver.handleIntent(mSession, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MediaServiceBinder();
    }

    public class Actions {
        static final String CHANGE_STATION = "change_station";
    }

    private SimpleExoPlayer preparedExoPlayerFromURL(String streamUrl) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "Radiom");
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(streamUrl));

        SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(MediaService.this).build();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(mediaSource, false, false);
        return exoPlayer;
    }

    void refreshNotificationAndForegroundStatus(int playbackState) {
        Log.d(TAG, "refreshing notification");
        switch (playbackState) {
            case PlaybackStateCompat.STATE_PLAYING: {
                startForeground(NOTIFICATION_ID, getNotification(playbackState));
                break;
            }
            case PlaybackStateCompat.STATE_PAUSED: {
                NotificationManagerCompat.from(MediaService.this)
                        .notify(NOTIFICATION_ID, getNotification(playbackState));
                stopForeground(false);
                break;
            }
            default: {
                stopForeground(true);
                break;
            }
        }
    }

    Notification getNotification(int playbackState) {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mSession);

        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_previous, ACTION_PREVIOUS,
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));

        if (playbackState == PlaybackStateCompat.STATE_PLAYING)
            builder.addAction(
                    new NotificationCompat.Action(
                            android.R.drawable.ic_media_pause, ACTION_PAUSE,
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        else
            builder.addAction(
                    new NotificationCompat.Action(
                            android.R.drawable.ic_media_play, ACTION_PLAY,
                            MediaButtonReceiver.buildMediaButtonPendingIntent(
                                    this,
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE)));


        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_next, ACTION_NEXT,
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        builder
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(2)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(
                                MediaButtonReceiver.buildMediaButtonPendingIntent(
                                        this,
                                        PlaybackStateCompat.ACTION_STOP))
                        .setMediaSession(mSession.getSessionToken()))
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true);

        return builder.build();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    class MediaServiceBinder extends Binder {

        MediaSessionCompat.Token getMediaSessionToken() {
            return mSession.getSessionToken();
        }
    }
}