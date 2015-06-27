package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by naren on 27/06/15.
 */
public class SongListAdapter extends BaseAdapter implements Filterable{
    private ArrayList<Song> all_songs = null;
    private ArrayList<Song> songs = null;
    private LayoutInflater songLayoutInf = null;

    public SongListAdapter(Context c, ArrayList<Song> songs) {
        this.all_songs = songs;
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


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Song> filteredresults = new ArrayList<Song>();
                for(int i=0; i<all_songs.size(); i++) {
                    if (all_songs.get(i).getArtist().toLowerCase().contains(constraint.toString().toLowerCase()) ||
                            all_songs.get(i).getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredresults.add(all_songs.get(i));
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredresults;
                results.count = filteredresults.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                songs = (ArrayList<Song>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
