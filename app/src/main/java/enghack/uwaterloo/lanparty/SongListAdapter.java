package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by naren on 27/06/15.
 */
public class SongListAdapter extends BaseAdapter {

    private ArrayList<Song> songs = null;
    private LayoutInflater songLayoutInf = null;

    public SongListAdapter(Context c, ArrayList<Song> songs) {
        this.songs = songs;
        this.songLayoutInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLL = (LinearLayout)songLayoutInf.inflate(R.layout.song_entry, parent, false);

        TextView title = (TextView) songLL.findViewById(R.id.song_title);
        TextView artist = (TextView) songLL.findViewById(R.id.song_artist);
        Song current = songs.get(position);

        title.setText(current.getTitle());
        artist.setText(current.getArtist());

        songLL.setTag(position);

        return songLL;
    }


}
