package pandit.mediaplayer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

public class AudioDetails {
    private String audioName;
    private String audioTime;
    private String audioAlbum;
    private Bitmap AlbumBitmap;

    Bitmap getAlbumBitmap() {
        return AlbumBitmap;
    }

    void setAlbumBitmap(Bitmap albumBitmap) {
        AlbumBitmap = albumBitmap;
    }

    String getAudioName() {
        return audioName;
    }

    void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    String getAudioTime() {
        return audioTime;
    }

    void setAudioTime(String audioTime) {
        this.audioTime = convertDuration(audioTime);
    }

    String getAudioAlbum() {
        return audioAlbum;
    }

    void setAudioAlbum(String audioAlbum) {
        this.audioAlbum = audioAlbum;
    }

    @NonNull
    @Override
    public String toString() {
        return "audioName='" + audioName + '\n' +
                "audioTime='" + audioTime + '\n' +
                "audioAlbum='" + audioAlbum;
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
