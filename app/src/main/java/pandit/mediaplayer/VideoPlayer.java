package pandit.mediaplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.OrientationEventListener;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {
    private VideoView mVideoView;
    private boolean isPlaying = false;
    private Orientation mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mVideoView = findViewById(R.id.VideoView);

        if (getIntent().getExtras() != null) {
            PlayAudioFile(getIntent().getExtras().getString("Video"));
        }

        MediaController mediaController = new MediaController(VideoPlayer.this, true);
        mVideoView.setMediaController(mediaController);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

        mOrientation = new Orientation(VideoPlayer.this);
    }

    void PlayAudioFile(String AudioFile) {
        ContentResolver contentResolver = getContentResolver();
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor videoCursor = contentResolver.query(videoUri, null, null, null, null);
        if (videoCursor != null && videoCursor.moveToFirst()) {
            do {
                if ((videoCursor.getString(videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE))).equals(AudioFile)) {
                    System.gc();
                    mVideoView.setVideoPath(videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                    mVideoView.start();
                    isPlaying = true;
                }
            } while (videoCursor.moveToNext());
        }
        if (videoCursor != null) {
            videoCursor.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mOrientation.enable();
    }

    @Override
    protected void onStop() {
        mOrientation.disable();
        super.onStop();
    }

    @Override
    protected void onUserLeaveHint() {
        if (isPlaying) {
            enterPictureInPictureMode();
        }
    }

    private class Orientation extends OrientationEventListener {
        final int ROTATION_O = 1;
        final int ROTATION_90 = 2;
        final int ROTATION_180 = 3;
        final int ROTATION_270 = 4;
        private int rotation = 0;

        Orientation(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if ((orientation == 0) && rotation != ROTATION_O) {
                rotation = ROTATION_O;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (orientation == 180 && rotation != ROTATION_180) {
                rotation = ROTATION_180;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            } else if (orientation == 90 && rotation != ROTATION_270) {
                rotation = ROTATION_270;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            } else if (orientation == 270 && rotation != ROTATION_90) {
                rotation = ROTATION_90;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }
}
