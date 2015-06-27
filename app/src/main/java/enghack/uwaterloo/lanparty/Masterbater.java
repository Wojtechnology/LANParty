package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by wojtekswiderski on 15-06-27.
 */
public class Masterbater {

    private Server mServer;
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private SongQueue mSongQueue;

    public Masterbater(Context context) {
        mContext = context;
        mSongQueue = new SongQueue();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });
        try {
            mServer = new Server(mContext, this);
            mServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Nope", "The server did not run!");
        }
    }

    public SongQueue getSongQueue() {
        return mSongQueue;
    }

    public void addSong(Song song){
        mSongQueue.push_back(song);
        if (mSongQueue.size() == 1 && !mMediaPlayer.isPlaying()){
            playSong(song);
        }
        updateQueue();
    }

    private void updateQueue(){
        MainActivity main = (MainActivity) mContext;
        if (main.mQueueAdapter != null) {
            final MainActivity finalMain = main;
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalMain.mQueueAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void playSong(Song song){
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(song.getUri());
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
            ((MainActivity) mContext).setBottomBar(song.getArtist(), song.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    public void stop() {
        mServer.stop();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        ((MainActivity) mContext).setBottomBar("", "");
    }

    public void playNext(){
        Song oldSong = mSongQueue.pop_front();
        if (oldSong == null) return;
        try {
            mContext.deleteFile(oldSong.getFile().getName());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (mSongQueue.size() > 0){
            playSong(mSongQueue.peek_front());
        } else {
            mMediaPlayer.stop();
            ((MainActivity) mContext).setBottomBar("", "");
        }
        updateQueue();
    }

    public boolean togglePlay(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            return false;
        }else{
            mMediaPlayer.start();
            return true;
        }
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

}
