package pandit.mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

class Notify {
    private Context mContext;
    private NotificationManager notificationManager;

    Notify(Context context, NotificationManager notificationManager) {
        mContext = context;
        this.notificationManager = notificationManager;
    }

    void create(String songName) {
        NotificationChannel mChannel = null;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "1001");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("101", "Ram", NotificationManager.IMPORTANCE_DEFAULT);
            builder.setChannelId("101");
        }

        builder.setContentTitle("Now Playing...")
                .setContentText(songName)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_VIBRATE | Notification.FLAG_ONGOING_EVENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(1001, notification);
    }
}
