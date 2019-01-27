package pandit.mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

class Notify {
    private String SongName;
    private Context mContext;
    private NotificationManager notificationManager;

    Notify(Context context, String songName, NotificationManager notificationManager) {
        SongName = songName;
        mContext = context;
        this.notificationManager = notificationManager;
    }

    void create() {
        NotificationChannel mChannel = null;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "1001");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("101", "Ram", NotificationManager.IMPORTANCE_HIGH);
            builder.setChannelId("101");
        }

        builder.setContentTitle("Now Playing...")
                .setContentText(SongName)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL | Notification.FLAG_ONGOING_EVENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(1001, notification);
    }
}
