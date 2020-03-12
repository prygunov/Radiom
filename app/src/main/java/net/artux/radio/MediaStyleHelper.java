package net.artux.radio;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

class MediaStyleHelper {

    static NotificationCompat.Builder from(
            Context context, MediaSessionCompat mediaSession) {
        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = notificationManager.getNotificationChannel("radiom_channel");
            if (mChannel == null) {
                mChannel = new NotificationChannel("radiom_channel", "radiom",  NotificationManager.IMPORTANCE_LOW);
                mChannel.setDescription("Artux Radio");
                mChannel.enableLights(false);
                mChannel.enableVibration(false);
                mChannel.setSound(null, null);
                notificationManager.createNotificationChannel(mChannel);
            }

        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "radiom_channel");
        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(context, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setChannelId("radiom_channel");
        return builder;
    }
}
