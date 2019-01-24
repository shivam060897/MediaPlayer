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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;

public class VideoPlayer extends AppCompatActivity {
    public int position, maxPosition;
    int Playing = 0;
    ArrayList<String> arrayList;
    ListView listView1;
    VideoView videoView1;
    MediaController mediaController;
    OrientationListener orientationListener;
    ArrayAdapter<String> adapter;
    int video_column_index;
    String filename;
    Cursor videoCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        listView1 = findViewById(R.id.lv1);
        videoView1 = findViewById(R.id.vv1);
        arrayList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        videoCursor = contentResolver.query(videoUri, null, null, null, null);
        if (videoCursor != null && videoCursor.moveToFirst()) {
            int songTitle = videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            do {
                String currentTitle = videoCursor.getString(songTitle);
                arrayList.add(currentTitle);
            } while (videoCursor.moveToNext());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView1.setAdapter(adapter);
        maxPosition = adapter.getCount();
        maxPosition = maxPosition - 1;
        position = getIntent().getExtras().getInt("position");
        orientationListener = new OrientationListener(this);
        mediaController = new MediaController(VideoPlayer.this, true);
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev();
            }
        });
        videoView1.setMediaController(mediaController);
        listView1.setSelection(position);
        System.gc();
        video_column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        videoCursor.moveToPosition(position);
        filename = videoCursor.getString(video_column_index);
        videoView1.setVideoPath(filename);
        videoView1.start();
        Playing = 1;
        videoView1.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        orientationListener.enable();
    }

    @Override
    protected void onStop() {
        orientationListener.disable();
        super.onStop();
    }

    public void next() {
        if (position + 1 <= maxPosition) {
            position = position + 1;
            listView1.setSelection(position);
            System.gc();
            video_column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            filename = videoCursor.getString(video_column_index);
            videoView1.setVideoPath(filename);
            videoView1.start();
            Playing = 1;
        } else if (position == maxPosition) {
            position = 0;
            listView1.setSelection(position);
            System.gc();
            video_column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            filename = videoCursor.getString(video_column_index);
            videoView1.setVideoPath(filename);
            videoView1.start();
            Playing = 1;
        }
    }

    public void prev() {
        if (position - 1 > -1) {
            position = position - 1;
            listView1.setSelection(position);
            System.gc();
            video_column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            filename = videoCursor.getString(video_column_index);
            videoView1.setVideoPath(filename);
            videoView1.start();
            Playing = 1;
        } else if (position == 0) {
            position = maxPosition;
            listView1.setSelection(position);
            System.gc();
            video_column_index = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            filename = videoCursor.getString(video_column_index);
            videoView1.setVideoPath(filename);
            videoView1.start();
            Playing = 1;
        }
    }

    @Override
    protected void onUserLeaveHint() {
        if (Playing == 1) {
            enterPictureInPictureMode();
        }
    }

    private class OrientationListener extends OrientationEventListener {
        final int ROTATION_O = 1;
        final int ROTATION_90 = 2;
        final int ROTATION_180 = 3;
        final int ROTATION_270 = 4;
        private int rotation = 0;

        OrientationListener(Context context) {
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