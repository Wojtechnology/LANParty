package enghack.uwaterloo.lanparty;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
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
        Map<String, List<String>> decodedQueryParameters =
                decodeParameters(session.getQueryParameterString());

        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head><title>Debug Server</title></head>");
        sb.append("<body>");
        sb.append("<h1>Debug Server</h1>");

        sb.append("<p><blockquote><b>URI</b> = ").append(
                String.valueOf(session.getUri())).append("<br />");

        sb.append("<b>Method</b> = ").append(
                String.valueOf(session.getMethod())).append("</blockquote></p>");

        sb.append("<h3>Headers</h3><p><blockquote>").
                append(session.getHeaders().toString()).append("</blockquote></p>");

        sb.append("<h3>Parms</h3><p><blockquote>").
                append(session.getParms().toString()).append("</blockquote></p>");

        sb.append("<h3>Parms (multi values?)</h3><p><blockquote>").
                append(decodedQueryParameters.toString()).append("</blockquote></p>");

        Map<String, String> files = null;
        try {
            files = new HashMap<String, String>();
            session.parseBody(files);
            sb.append("<h3>Files</h3><p><blockquote>").
                    append(files.toString()).append("</blockquote></p>");
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
        if (uri.equals("/queue")) {
            if(method.equals("GET")) {
                //return queue
            }

            else if(method.equals("POST")) {
                //Post to queue
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

    /*@Override
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
    }*/
}
