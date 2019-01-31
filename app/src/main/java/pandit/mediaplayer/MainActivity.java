package pandit.mediaplayer;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSION_REQUEST = 1;
    private static MediaPlayer mMediaPlayer;
    private ConstraintLayout mConstraintLayout;
    private Button Music;
    private Button Video;
    private ListView VideoList;
    private ListView AudioList;
    private ImageButton Repeat;
    private ImageButton rewind;
    private ImageButton play_pause;
    private ImageButton f_forward;
    private ImageButton Shuffle;
    private TextView ElapsedTime;
    private TextView TotalTime;
    private TextView FileName;
    private SeekBar Progress;
    private ArrayList<AudioDetails> mAudioDetails = new ArrayList<>();
    private ArrayList<VideoDetails> mVideoDetails = new ArrayList<>();
    private Cursor AudioCursor;
    private String Path;
    private Handler handler = new Handler();
    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if (mMediaPlayer.isPlaying()) {
                getCurrentMaxTime(mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
                Progress.setVisibility(View.VISIBLE);
                Progress.setMax(mMediaPlayer.getDuration());
                Progress.setProgress(mMediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        }
    };
    private int play = 0, Position, shuffle = 0, repeat = 0, maxPosition;
    private Notify mNotify;

    void init() {
        Music = findViewById(R.id.Music);
        Video = findViewById(R.id.Video);
        VideoList = findViewById(R.id.VideoList);
        AudioList = findViewById(R.id.AudioList);
        mConstraintLayout = findViewById(R.id.PlayLay);
        Music.performClick();
        Repeat = findViewById(R.id.prev);
        rewind = findViewById(R.id.rewind);
        play_pause = findViewById(R.id.play_pause);
        f_forward = findViewById(R.id.fast_forward);
        Shuffle = findViewById(R.id.next);
        ElapsedTime = findViewById(R.id.Elapsed_Time);
        TotalTime = findViewById(R.id.Total_Time);
        Progress = findViewById(R.id.Progress);
        FileName = findViewById(R.id.FileName);
        mMediaPlayer = new MediaPlayer();
        mNotify = new Notify(MainActivity.this, (NotificationManager) getSystemService(NOTIFICATION_SERVICE));
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

        VideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                VideoDetails videoDetails = mVideoDetails.get(position);
                Intent intent = new Intent(MainActivity.this, VideoPlayer.class);
                intent.putExtra("Video", videoDetails.getName());
                startActivity(intent);
            }
        });

        AudioList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioCursor.moveToPosition(position);
                Path = AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                playAudio(Path, AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
            }
        });

        Music.performClick();

        Repeat.setOnClickListener(this);
        rewind.setOnClickListener(this);
        f_forward.setOnClickListener(this);
        Shuffle.setOnClickListener(this);
        play_pause.setOnClickListener(this);

        generateList("Audio");
        generateList("Video");
    }

    public void getCurrentMaxTime(int c, int max) {
        int min1, sec1, x1;
        String time1;
        String time2;
        x1 = c / 1000;
        min1 = x1 / 60;
        sec1 = x1 - min1 * 60;
        if (sec1 >= 0 && sec1 <= 9) {
            time1 = Integer.toString(min1) + ":0" + Integer.toString(sec1);
        } else {
            time1 = Integer.toString(min1) + ":" + Integer.toString(sec1);
        }
        ElapsedTime.setText(time1);
        int min2, sec2, x2;
        x2 = max / 1000;
        min2 = x2 / 60;
        sec2 = x2 - min2 * 60;
        if (sec2 >= 0 && sec2 <= 9) {
            time2 = Integer.toString(min2) + ":0" + Integer.toString(sec2);
        } else {
            time2 = Integer.toString(min2) + ":" + Integer.toString(sec2);
        }
        TotalTime.setText(time2);
    }

    private void playAudio(String path, String FileName) {
        Progress.setProgress(0);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        this.FileName.setText(FileName);

        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mNotify.create(FileName);
            handler.removeCallbacks(moveSeekBarThread);
            handler.postDelayed(moveSeekBarThread, 100);
            play_pause.setImageResource(getResources().getIdentifier("ic_pause_black_24dp", "drawable", getPackageName()));
        } catch (IOException e) {
            Toast.makeText(this, "Can't play this file", Toast.LENGTH_SHORT).show();
        }
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
        if (type.equals("Audio")) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            AudioCursor = contentResolver.query(uri, null, null, null, null);
            if (AudioCursor != null && AudioCursor.moveToFirst()) {
                int songTitle = AudioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int songDuration = AudioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int songAlbum = AudioCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                do {
                    AudioDetails audioDetails = new AudioDetails();
                    audioDetails.setAudioName(AudioCursor.getString(songTitle));
                    audioDetails.setAudioTime(AudioCursor.getString(songDuration));
                    audioDetails.setAudioAlbum(AudioCursor.getString(songAlbum));
                    mAudioDetails.add(audioDetails);
                } while (AudioCursor.moveToNext());
            }

            CustomAudioAdaptor customAudioAdaptor = new CustomAudioAdaptor(MainActivity.this, mAudioDetails);
            AudioList.setAdapter(customAudioAdaptor);
            maxPosition = customAudioAdaptor.getCount();
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            Cursor videoCursor = contentResolver.query(uri, null, null, null, null);

            if (videoCursor != null && videoCursor.moveToFirst()) {
                int songTitle = videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                int songTime = videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                do {
                    VideoDetails videoDetails = new VideoDetails();
                    videoDetails.setName(videoCursor.getString(songTitle));
                    videoDetails.setDuration(videoCursor.getString(songTime));
                    mVideoDetails.add(videoDetails);
                } while (videoCursor.moveToNext());
            }

            if (videoCursor != null) {
                videoCursor.close();
            }

            CustomVideoAdaptor customVideoAdaptor = new CustomVideoAdaptor(MainActivity.this, mVideoDetails);
            VideoList.setAdapter(customVideoAdaptor);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.Music: {
                mConstraintLayout.setVisibility(View.VISIBLE);
                VideoList.setVisibility(View.INVISIBLE);
                view.setBackgroundResource(R.drawable.selection_back);
                Video.setBackgroundResource(R.drawable.selection_back2);
            }
            break;
            case R.id.Video: {
                mConstraintLayout.setVisibility(View.GONE);
                VideoList.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.selection_back);
                Music.setBackgroundResource(R.drawable.selection_back2);
            }
            break;
            case R.id.prev: {
                if (repeat == 0) {
                    repeat = 1;
                    Repeat.setImageResource(getResources().getIdentifier("ic_repeat_one_black_24dp", "drawable", getPackageName()));
                    mMediaPlayer.setLooping(true);
                } else if (repeat == 1) {
                    repeat = 2;
                    Repeat.setImageResource(getResources().getIdentifier("ic_repeat_red_24dp", "drawable", getPackageName()));
                    mMediaPlayer.setLooping(false);
                } else if (repeat == 2) {
                    repeat = 0;
                    Repeat.setImageResource(getResources().getIdentifier("sharp_repeat_24_white", "drawable", getPackageName()));
                    mMediaPlayer.setLooping(false);
                }
            }
            break;
            case R.id.rewind: {
                if (Position - 1 > -1) {
                    Position = Position - 1;
                    System.gc();
                    AudioCursor.moveToPosition(Position);
                    playAudio(AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)), AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                } else if (Position == 0) {
                    Position = maxPosition;
                    System.gc();
                    playAudio(AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)), AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        rewind.setImageResource(getResources().getIdentifier("sharp_skip_previous_24_white", "drawable", getPackageName()));
                    }
                }, 100);
                rewind.setImageResource(getResources().getIdentifier("sharp_skip_previous_24_red", "drawable", getPackageName()));
            }
            break;
            case R.id.play_pause: {
                if (play == 0) {
                    play = 1;
                    mMediaPlayer.start();
                    handler.removeCallbacks(moveSeekBarThread);
                    handler.postDelayed(moveSeekBarThread, 100);
                    play_pause.setImageResource(getResources().getIdentifier("ic_pause_black_24dp", "drawable", getPackageName()));
                } else if (play == 1) {
                    play = 0;
                    mMediaPlayer.pause();
                    play_pause.setImageResource(getResources().getIdentifier("ic_play_arrow_black_24dp", "drawable", getPackageName()));
                }
            }
            break;
            case R.id.fast_forward: {
                if (Position + 1 <= maxPosition) {
                    Position = Position + 1;
                    System.gc();
                    AudioCursor.moveToPosition(Position);
                    playAudio(AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)), AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                } else if (Position == maxPosition) {
                    Position = 0;
                    System.gc();
                    AudioCursor.moveToPosition(Position);
                    playAudio(AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)), AudioCursor.getString(AudioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        f_forward.setImageResource(getResources().getIdentifier("sharp_skip_next_24_white", "drawable", getPackageName()));
                    }
                }, 100);
                f_forward.setImageResource(getResources().getIdentifier("sharp_skip_next_24_red", "drawable", getPackageName()));
            }
            break;
            case R.id.next: {
                if (shuffle == 0) {
                    shuffle = 1;
                    Shuffle.setImageResource(getResources().getIdentifier("ic_shuffle_red_24dp", "drawable", getPackageName()));
                } else if (shuffle == 1) {
                    shuffle = 0;
                    Shuffle.setImageResource(getResources().getIdentifier("sharp_shuffle_24_white", "drawable", getPackageName()));
                }
            }
            break;
        }
    }
}
