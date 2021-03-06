package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by naren on 26/06/15.
 */
public class Server extends NanoHTTPD {

    private Context mContext;
    private Masterbater mMasterbater;

    public Server(Context context, Masterbater master) throws IOException
    {
        super(8080);
        mMasterbater = master;
        mContext = context;
    }

    @Override public Response serve(IHTTPSession session) {
        Map<String, List<String>> decodedQueryParameters = null;
        final MainActivity main = (MainActivity) mContext;

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
                String jsonstr = uri.substring(7);
                Log.e("JSONSTR", jsonstr);
                JSONObject metadata = null;
                try {
                    metadata = new JSONObject(jsonstr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e("Artist", metadata.optString("artist"));
                Log.e("Title", metadata.optString("title"));

                // Shows where the song came from
                final Map<String, String> finalHeader = header;
                final JSONObject finalMetadata = metadata;
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, finalMetadata.optString("title") + " received from " + finalHeader.get("http-client-ip"), Toast.LENGTH_SHORT).show();
                        main.setBottomBar(finalMetadata.optString("artist"), finalMetadata.optString("title"));
                    }
                });
                //Saving to device
                File destinationFolder = new File(main.getFilesDir(), metadata.optString("title") + ".mp3");
                try {
                    destinationFolder.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File sourceFolder = new File(files.get("song"));
                InputStream in = null;
                OutputStream out = null;
                try {
                    Log.e("Creating", destinationFolder.getName());
                    in = new FileInputStream(sourceFolder);
                    out = new FileOutputStream(destinationFolder);
                    byte[] buffer = new byte[1024];
                    int len;
                    while((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);

                    }
                    in.close();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Song newSong = new Song(69, metadata.optString("title"), metadata.optString("artist"), destinationFolder.getPath());
                newSong.setFile(destinationFolder);
                mMasterbater.addSong(newSong);

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
