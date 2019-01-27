package pandit.mediaplayer;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

public class VideoDetails {
    private String Name;
    private String Duration;
    private Bitmap AlbumImage;
    private Uri uri;

    public Bitmap getAlbumImage() {
        return AlbumImage;
    }

    public void setAlbumImage(Bitmap albumImage) {
        AlbumImage = albumImage;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDuration() {
        return convertDuration(Duration);
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    @NonNull
    @Override
    public String toString() {
        return "mImage=" + "\n" + "Name='" + Name + "\n" + "Duration='" + Duration;
    }

    private String convertDuration(String duration) {
        int dur = Integer.parseInt(duration);
        int sec = dur / 1000;
        int hour = sec / 3600;
        if (hour > 0)
            sec = sec - hour * 3600;
        int min = sec / 60;
        if (min > 0)
            sec = sec - min * 60;

        String sec1;
        String min1;
        String songTime;
        if (sec < 10)
            sec1 = "0" + Integer.toString(sec);
        else
            sec1 = Integer.toString(sec);

        if (min < 10)
            min1 = "0" + Integer.toString(min);
        else
            min1 = Integer.toString(min);

        if (hour >= 1) {
            songTime = Integer.toString(hour) + ":" + min1 + ":" + sec1;
        } else {
            songTime = min1 + ":" + sec1;
        }

        return songTime;
    }
}
