package enghack.uwaterloo.lanparty;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by naren on 26/06/15.
 */
public class Server extends NanoHTTPD {
    public Server() throws IOException
    {
        super(8080);
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
        String answer = "Hello World";

        return new NanoHTTPD.Response(answer);
    }
}
