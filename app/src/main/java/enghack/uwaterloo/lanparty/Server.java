package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by naren on 26/06/15.
 */
public class Server extends NanoHTTPD {

    private Context mContext;

    public Server(Context context) throws IOException
    {
        super(8080);
        mContext = context;
    }

    @Override public Response serve(IHTTPSession session) {
        Map<String, List<String>> decodedQueryParameters = null;

        Map<String, String> files = null;
        try {
            files = new HashMap<String, String>();
            session.parseBody(files);
            decodedQueryParameters =
                    decodeParameters(session.getQueryParameterString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String uri = String.valueOf(session.getUri());
        String method = String.valueOf(session.getMethod());
        Map<String, String> header = session.getHeaders();
        Map<String, String> parms = session.getParms();

        Log.e("URI", uri);
        Log.e("Method", method);
        Log.e("Headers", header.toString());
        Log.e("Decoded Params", decodedQueryParameters.toString());
        Log.e("Parameters", parms.toString());
        Log.e("Files", files.toString());
        String answer;
        if (decodedQueryParameters.get("name") != null) {
            answer = "Hello " + decodedQueryParameters.get("name");
        }
        else {
            answer = "Hello World";
        }
        if (uri.startsWith("/queue")) {
            if(method.equals("GET")) {
                //return queue
            }

            else if(method.equals("POST")) {
                String[] params = uri.split("/");
                Log.e("Params", Arrays.toString(params));
                String artist = params[3];
                String title = params[2];
                Log.e("Artist", artist);
                Log.e("Title", title);
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(files.get("song"));
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
            }
            else if(method.equals("DELETE")) {
                //Delete works
            }
        }
        if (uri.equals("/connect")) {
            if(method.equals("GET")) {
                answer = "Connected!";
                return new NanoHTTPD.Response(Response.Status.OK, MIME_PLAINTEXT, answer);
            }
        }
        return new NanoHTTPD.Response(answer);
    }

}
