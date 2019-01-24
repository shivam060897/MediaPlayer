package pandit.mediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        return convertView;
    }

    private class ViewHolder {
        final TextView Name;
        final TextView Duration;
        final TextView Album;

        ViewHolder(View v) {
            this.Name = v.findViewById(R.id.AudioName);
            this.Duration = v.findViewById(R.id.AudioDuration);
            this.Album = v.findViewById(R.id.audioAlbum);
        }
    }
}
