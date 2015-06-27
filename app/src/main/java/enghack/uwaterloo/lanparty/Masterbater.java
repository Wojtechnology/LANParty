package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v4.media.MediaMetadataCompat;
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
                Song oldSong = mSongQueue.pop_front();
                mContext.deleteFile(oldSong.getUri());
                if (mSongQueue.size() > 0){
                    playSong(mSongQueue.peek_front());
                }
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

    public void addSong(Song song){
        mSongQueue.push_back(song);
        if (mSongQueue.size() == 0 && !mMediaPlayer.isPlaying()){
            playSong(song);
        }
    }

    public void playSong(Song song){
        try {
            mMediaPlayer.setDataSource(song.getUri());
            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    public void stop() {
        mServer.stop();
        mMediaPlayer.stop();
    }

}
