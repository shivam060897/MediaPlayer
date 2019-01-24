package pandit.mediaplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class VideoList extends AppCompatActivity {
    ListView listView1;
    CustomVideoAdaptor mCustomVideoAdaptor;
    ArrayList<VideoDetails> mVideoDetailsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        listView1 = findViewById(R.id.lv1);
        mVideoDetailsArrayList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        final Cursor videoCursor = contentResolver.query(videoUri, null, null, null, null);
        if (videoCursor != null && videoCursor.moveToFirst()) {
            int songTitle = videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            int songTime = videoCursor.getColumnIndex(MediaStore.Video.Media.DURATION);
            do {
                VideoDetails videoDetails = new VideoDetails();
                videoDetails.setName(videoCursor.getString(songTitle));
                videoDetails.setDuration(videoCursor.getString(songTime));
                mVideoDetailsArrayList.add(videoDetails);
            } while (videoCursor.moveToNext());
        }
        if (videoCursor != null)
            videoCursor.close();
        mCustomVideoAdaptor = new CustomVideoAdaptor(VideoList.this, mVideoDetailsArrayList);
        listView1.setAdapter(mCustomVideoAdaptor);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VideoList.this, VideoPlayer.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}