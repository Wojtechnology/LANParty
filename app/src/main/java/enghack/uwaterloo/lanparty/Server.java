package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
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

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          final Map<String, String> files) {
        final MainActivity main = (MainActivity) mContext;
        Log.e("URI", uri);
        Log.e("Method", method.toString());
        Log.e("Headers", header.toString());
        Log.e("Parameters", parameters.toString());
        Log.e("Files", files.toString());
        String answer;
        if (parameters.get("name") != null) {
            answer = "Hello " + parameters.get("name");
        }
        else {
            answer = "Hello World";
        }
        if (uri.equals("/queue")) {
            if(method.toString().equals("GET")) {
                //return queue
            }

            else if(method.toString().equals("POST")) {
                //Post to queue
            }
            else if(method.toString().equals("DELETE")) {
                //Delete works
            }
        }
        if (uri.equals("/connect")) {
            if(method.toString().equals("GET")) {
                answer = "Connected!";
                return new NanoHTTPD.Response(Response.Status.OK, MIME_PLAINTEXT, answer);
            }
        }
        return new NanoHTTPD.Response(answer);
    }
}
