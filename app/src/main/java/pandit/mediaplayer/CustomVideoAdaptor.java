package pandit.mediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomVideoAdaptor extends ArrayAdapter {
    private final int layoutResource;
    private final LayoutInflater mLayoutInflater;
    private List<VideoDetails> Data;

    CustomVideoAdaptor(Context context, List<VideoDetails> data) {
        super(context, R.layout.videoitemlist);
        this.layoutResource = R.layout.videoitemlist;
        mLayoutInflater = LayoutInflater.from(context);
        this.Data = data;
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        VideoDetails currentData = Data.get(position);

        viewHolder.Name.setText(currentData.getName());
        viewHolder.Duration.setText(currentData.getDuration());


        return convertView;
    }

    private class ViewHolder {
        final TextView Name;
        final TextView Duration;

        ViewHolder(View v) {
            this.Name = v.findViewById(R.id.VideoName);
            this.Duration = v.findViewById(R.id.VideoDuration);

        }
    }
}
