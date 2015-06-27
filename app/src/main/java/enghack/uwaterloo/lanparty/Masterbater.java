package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

/**
 * Created by wojtekswiderski on 15-06-27.
 */
public class Masterbater {

    private Server mServer;
    private Context mContext;

    public Masterbater(Context context) {
        mContext = context;
        try {
            mServer = new Server(mContext);
            mServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Nope", "The server did not run!");
        }
    }

    public void stop() {
        mServer.stop();
    }

}
