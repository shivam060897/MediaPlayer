package pandit.mediaplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAudioAdaptor extends ArrayAdapter {
    private final int layoutResource;
    private final LayoutInflater mLayoutInflater;
    private List<AudioDetails> Data;

    CustomAudioAdaptor(Context context, List<AudioDetails> audioDetailsList) {
        super(context, R.layout.audioitemlist);
        this.layoutResource = R.layout.audioitemlist;
        mLayoutInflater = LayoutInflater.from(context);
        this.Data = audioDetailsList;
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        CustomAudioAdaptor.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new CustomAudioAdaptor.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomAudioAdaptor.ViewHolder) convertView.getTag();
        }

        AudioDetails currentData = Data.get(position);

        viewHolder.Name.setText(currentData.getAudioName());
        viewHolder.Duration.setText(currentData.getAudioTime());
        viewHolder.Album.setText(currentData.getAudioAlbum());
        viewHolder.AlbumImage.setImageBitmap(currentData.getAlbumBitmap());

        return convertView;
    }

    private class ViewHolder {
        final TextView Name;
        final TextView Duration;
        final TextView Album;
        final ImageView AlbumImage;

        ViewHolder(View v) {
            this.Name = v.findViewById(R.id.AudioName);
            this.Duration = v.findViewById(R.id.AudioDuration);
            this.Album = v.findViewById(R.id.audioAlbum);
            this.AlbumImage = v.findViewById(R.id.AudioThumbnail);
        }
    }
}
