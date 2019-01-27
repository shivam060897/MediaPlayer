package pandit.mediaplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSION_REQUEST = 1;
    private Button Music;
    private Button Video;
    private ListView List;
    private ArrayList<AudioDetails> mAudioDetails = new ArrayList<>();
    private ArrayList<VideoDetails> mVideoDetails = new ArrayList<>();
    private boolean audio = true;

    void init() {
        Music = findViewById(R.id.Music);
        Video = findViewById(R.id.Video);
        List = findViewById(R.id.List);
        Music.performClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        }

        Music.setOnClickListener(this);
        Video.setOnClickListener(this);

        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (audio) {
                    Intent intent = new Intent(MainActivity.this, Activity_AudioPlayer.class);
                    intent.putExtra("Audio", mAudioDetails);
                    intent.putExtra("Audio File", position);
                    startActivity(intent);
                } else {
                    VideoDetails videoDetails = mVideoDetails.get(position);
                    Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
                    intent.putExtra("Video", videoDetails.getName());
                    intent.putExtra("Video File", position);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    void generateList(String type) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri;
        Cursor cursor;
        if (type.equals("Music")) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int songAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                do {
                    AudioDetails audioDetails = new AudioDetails();
                    audioDetails.setAudioName(cursor.getString(songTitle));
                    audioDetails.setAudioTime(cursor.getString(songDuration));
                    audioDetails.setAudioAlbum(cursor.getString(songAlbum));
                    mAudioDetails.add(audioDetails);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            CustomAudioAdaptor customAudioAdaptor = new CustomAudioAdaptor(MainActivity.this, mAudioDetails);
            List.setAdapter(customAudioAdaptor);
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int songTitle = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                int songTime = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                do {
                    VideoDetails videoDetails = new VideoDetails();
                    videoDetails.setName(cursor.getString(songTitle));
                    videoDetails.setDuration(cursor.getString(songTime));
                    mVideoDetails.add(videoDetails);
                } while (cursor.moveToNext());
            }

            if (cursor != null) {
                cursor.close();
            }

            CustomVideoAdaptor customVideoAdaptor = new CustomVideoAdaptor(MainActivity.this, mVideoDetails);
            List.setAdapter(customVideoAdaptor);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.Music: {
                generateList("Audio");
                audio = true;
            }
            break;
            case R.id.Video: {
                generateList("Video");
                audio = false;
            }
            break;
        }
    }
}
