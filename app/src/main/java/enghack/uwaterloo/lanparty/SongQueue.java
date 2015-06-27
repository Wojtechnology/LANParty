package enghack.uwaterloo.lanparty;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by wojtekswiderski on 15-06-27.
 */
public class SongQueue {
    Queue<Song> mSongQueue;

    public SongQueue() {
        mSongQueue = new LinkedList<>();
    }

    public void push_back(Song newSong){
        mSongQueue.offer(newSong);
    }

    public Song peek_front(){
        return mSongQueue.peek();
    }

    public Song pop_front(){
        return mSongQueue.poll();
    }

    public int size() {
        return mSongQueue.size();
    }

    public List<Song> displayList(){
        List<Song> disList = new LinkedList<>(mSongQueue);
        return disList;
    }
}
