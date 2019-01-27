package pandit.mediaplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class AudioPlayer extends AppCompatActivity {
    static MediaPlayer mediaPlayer = null;
    ListView listView1;
    CustomAudioAdaptor mCustomAudioAdaptor;
    ArrayList<AudioDetails> mAudioDetailsArrayList;
    ImageButton ib1, ib2, ib3, ib4, ib5;
    TextView t1, t2, t3;
    SeekBar seekBar;
    int play = 0, music_column_index, filename_index, Position, maxPosition, shuffle = 0, repeat = 0, mediaPos_new, mediaMax_new;
    String path, filename;
    Cursor musicCursor;
    Handler handler = new Handler();
    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if (mediaPlayer.isPlaying()) {
                mediaPos_new = mediaPlayer.getCurrentPosition();
                mediaMax_new = mediaPlayer.getDuration();
                getCurrentMaxTime();
                seekBar.setVisibility(View.VISIBLE);
                seekBar.setMax(mediaMax_new);
                seekBar.setProgress(mediaPos_new);
                handler.postDelayed(this, 100);
            }
        }
    };

    void init() {
        listView1 = findViewById(R.id.lv1);
        ib1 = findViewById(R.id.ib1);
        ib2 = findViewById(R.id.ib2);
        ib3 = findViewById(R.id.ib3);
        ib4 = findViewById(R.id.ib4);
        ib5 = findViewById(R.id.ib5);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        seekBar = findViewById(R.id.pb1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        init();

        mediaPlayer = new MediaPlayer();
        mAudioDetailsArrayList = new ArrayList<>();
        t1.setVisibility(View.INVISIBLE);
        t2.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        generateAudioList();
        maxPosition = mCustomAudioAdaptor.getCount() - 1;

        ib1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat == 0) {
                    repeat = 1;
                    ib1.setImageResource(getResources().getIdentifier("ic_repeat_one_black_24dp",
                            "drawable", getPackageName()));
                    mediaPlayer.setLooping(true);
                } else if (repeat == 1) {
                    repeat = 2;
                    ib1.setImageResource(getResources().getIdentifier("ic_repeat_red_24dp",
                            "drawable", getPackageName()));
                    mediaPlayer.setLooping(false);
                } else if (repeat == 2) {
                    repeat = 0;
                    ib1.setImageResource(getResources().getIdentifier("sharp_repeat_24_white",
                            "drawable", getPackageName()));
                    mediaPlayer.setLooping(false);
                }
            }
        });

        ib2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Position - 1 > -1) {
                    Position = Position - 1;
                    System.gc();
                    music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    musicCursor.moveToPosition(Position);
                    path = musicCursor.getString(music_column_index);
                    filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    filename = musicCursor.getString(filename_index);
                    playAudio();
                } else if (Position == 0) {
                    Position = maxPosition;
                    System.gc();
                    music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    musicCursor.moveToPosition(Position);
                    path = musicCursor.getString(music_column_index);
                    filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    filename = musicCursor.getString(filename_index);
                    playAudio();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ib2.setImageResource(getResources().getIdentifier("sharp_skip_previous_24_white",
                                "drawable", getPackageName()));
                    }
                }, 100);
                ib2.setImageResource(getResources().getIdentifier("sharp_skip_previous_24_red",
                        "drawable", getPackageName()));
            }
        });

        ib3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play == 0) {
                    play = 1;
                    mediaPlayer.start();
                    handler.removeCallbacks(moveSeekBarThread);
                    handler.postDelayed(moveSeekBarThread, 100);
                    ib3.setImageResource(getResources().getIdentifier("ic_pause_black_24dp", "drawable", getPackageName()));
                } else if (play == 1) {
                    play = 0;
                    mediaPlayer.pause();
                    ib3.setImageResource(getResources().getIdentifier("ic_play_arrow_black_24dp", "drawable", getPackageName()));
                }
            }
        });

        ib4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Position + 1 <= maxPosition) {
                    Position = Position + 1;
                    System.gc();
                    music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    musicCursor.moveToPosition(Position);
                    path = musicCursor.getString(music_column_index);
                    filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    filename = musicCursor.getString(filename_index);
                    playAudio();
                } else if (Position == maxPosition) {
                    Position = 0;
                    System.gc();
                    music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    musicCursor.moveToPosition(Position);
                    path = musicCursor.getString(music_column_index);
                    filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    filename = musicCursor.getString(filename_index);
                    playAudio();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ib4.setImageResource(getResources().getIdentifier("sharp_skip_next_24_white",
                                "drawable", getPackageName()));
                    }
                }, 100);
                ib4.setImageResource(getResources().getIdentifier("sharp_skip_next_24_red",
                        "drawable", getPackageName()));
            }
        });

        ib5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffle == 0) {
                    shuffle = 1;
                    ib5.setImageResource(getResources().getIdentifier("ic_shuffle_red_24dp",
                            "drawable", getPackageName()));
                } else if (shuffle == 1) {
                    shuffle = 0;
                    ib5.setImageResource(getResources().getIdentifier("sharp_shuffle_24_white",
                            "drawable", getPackageName()));
                }
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Position = position;
                music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                musicCursor.moveToPosition(Position);
                path = musicCursor.getString(music_column_index);
                filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                filename = musicCursor.getString(filename_index);
                playAudio();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekBar.setProgress(progress);
                    mediaPlayer.seekTo(progress);
                    int min1, sec1, x1;
                    String time1;
                    x1 = progress / 1000;
                    min1 = x1 / 60;
                    sec1 = x1 - min1 * 60;
                    if (sec1 >= 0 && sec1 <= 9) {
                        time1 = Integer.toString(min1) + ":0" + Integer.toString(sec1);
                    } else {
                        time1 = Integer.toString(min1) + ":" + Integer.toString(sec1);
                    }
                    t1.setText(time1);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
                mediaPlayer.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (Position == maxPosition && repeat != 2)
                    mediaPlayer.stop();
                else if (Position == maxPosition) {
                    Position = 0;
                    System.gc();
                    music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    musicCursor.moveToPosition(Position);
                    path = musicCursor.getString(music_column_index);
                    filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    filename = musicCursor.getString(filename_index);
                    playAudio();
                } else
                    playNext();
            }
        });

    }

    private void generateAudioList() {
        ContentResolver contentResolver = getContentResolver();
        Uri videoUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        musicCursor = contentResolver.query(videoUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            int songTitle = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songDuration = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int songAlbum = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            Long albumId = musicCursor.getLong(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            do {
                AudioDetails audioDetails = new AudioDetails();
                audioDetails.setAudioName(musicCursor.getString(songTitle));
                audioDetails.setAudioTime(musicCursor.getString(songDuration));
                audioDetails.setAudioAlbum(musicCursor.getString(songAlbum));
                audioDetails.setAlbumBitmap(createAudioBitmap(albumId));
                mAudioDetailsArrayList.add(audioDetails);
            } while (musicCursor.moveToNext());
        }
        mCustomAudioAdaptor = new CustomAudioAdaptor(AudioPlayer.this, mAudioDetailsArrayList);
        listView1.setAdapter(mCustomAudioAdaptor);
    }

    Bitmap createAudioBitmap(Long albumId) {
        Bitmap bitmap;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId));
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.audio_file);
        }

        return bitmap;
    }

    public void playAudio() {
        seekBar.setProgress(0);
        mediaPlayer.stop();
        mediaPlayer.reset();
        t3.setText(filename);
        createNotification(filename);

        Notify notify = new Notify(AudioPlayer.this, filename, (NotificationManager) getSystemService(NOTIFICATION_SERVICE));
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            notify.create();
            play = 1;
            handler.removeCallbacks(moveSeekBarThread);
            handler.postDelayed(moveSeekBarThread, 100);
            ib3.setImageResource(getResources().getIdentifier("ic_pause_black_24dp", "drawable", getPackageName()));
        } catch (Exception e) {
            Toast.makeText(AudioPlayer.this, "Can't play this file", Toast.LENGTH_SHORT).show();
        }
    }

    public void getCurrentMaxTime() {
        t1.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        ib1.setVisibility(View.VISIBLE);
        int min1, sec1, x1;
        String time1;
        String time2;
        x1 = mediaPos_new / 1000;
        min1 = x1 / 60;
        sec1 = x1 - min1 * 60;
        if (sec1 >= 0 && sec1 <= 9) {
            time1 = Integer.toString(min1) + ":0" + Integer.toString(sec1);
        } else {
            time1 = Integer.toString(min1) + ":" + Integer.toString(sec1);
        }
        t1.setText(time1);
        int min2, sec2, x2;
        x2 = mediaMax_new / 1000;
        min2 = x2 / 60;
        sec2 = x2 - min2 * 60;
        if (sec2 >= 0 && sec2 <= 9) {
            time2 = Integer.toString(min2) + ":0" + Integer.toString(sec2);
        } else {
            time2 = Integer.toString(min2) + ":" + Integer.toString(sec2);
        }
        t2.setText(time2);
    }

    public void playNext() {
        Position = Position + 1;
        System.gc();
        music_column_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        musicCursor.moveToPosition(Position);
        path = musicCursor.getString(music_column_index);
        filename_index = musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        filename = musicCursor.getString(filename_index);
        playAudio();
    }

    public void createNotification(String songName) {
        NotificationChannel mChannel = null;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AudioPlayer.this, "1001");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("101", "Ram", NotificationManager.IMPORTANCE_HIGH);
            builder.setChannelId("101");
        }

        builder.setContentTitle("Now Playing...")
                .setContentText(songName)
                .setSmallIcon(R.drawable.notification_icon)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(1001, notification);
    }
}
