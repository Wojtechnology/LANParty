package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by naren on 27/06/15.
 */
public class QueueAdapter extends BaseAdapter{
    SongQueue queue = null;
    public LayoutInflater queueInflater = null;

    public QueueAdapter(Context c, SongQueue sQ) {
        queue = sQ;
        queueInflater = LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        return queue.mSongQueue.size();
    }

    @Override
    public Object getItem(int position) {
        return queue.mSongQueue.toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout queueLL = (LinearLayout)queueInflater.inflate(R.layout.song_entry, parent, false);

        TextView title = (TextView) queueLL.findViewById(R.id.song_title);
        TextView artist = (TextView) queueLL.findViewById(R.id.song_artist);
        Song current = (Song) queue.mSongQueue.toArray()[position];

        title.setText(current.getTitle());
        artist.setText(current.getArtist());

        queueLL.setTag(position);

        return queueLL;
    }
}
