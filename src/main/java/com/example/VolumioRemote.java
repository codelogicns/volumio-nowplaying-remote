package com.example;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class VolumioRemote {

    private static final String VOLHost = "http://127.0.0.1:3000"; // Local Volumio

    private static final Map<String, String> viewMap = Map.of(
            "song", "nowPlaying.infoView",
            "artist", "nowPlaying.infoView.artist",
            "album", "nowPlaying.infoView.album",
            "lyrics", "nowPlaying.infoView.lyrics"
    );

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Usage: java -jar volumio-remote.jar <song|artist|album|lyrics>");
            return;
        }

        String type = args[0];
        String view = viewMap.get(type);
        if (view == null) {
            System.err.println("{\"error\": \"Unknown view: " + type + "\"}");
            return;
        }

        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"};
        options.reconnection = false;
        options.timeout = 1000;

        Socket socket = IO.socket(VOLHost, options);

        socket.on(Socket.EVENT_CONNECT, args1 -> {
            try {
                executeMethod(socket, "configSaveStartupOptions",  view);
                executeMethod(socket, "broadcastRefresh",  null);
            } catch (InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args12 -> {
            System.err.println("{\"error\": \"Failed to connect\"}");
        });

        socket.on(Socket.EVENT_ERROR, args12 -> {
            System.err.printf("{\"error\": \"%s\"}", args12);
        });

        socket.on(Socket.EVENT_DISCONNECT, args12 -> {
            System.exit(0);
        });

        socket.connect();

        Thread.sleep(2000);
        socket.disconnect();
    }

    private static void executeMethod(Socket socket, String method, String view)
            throws URISyntaxException, InterruptedException {

        Map<String, Object> payload = new HashMap<>();
        payload.put("endpoint", "user_interface/now_playing");
        payload.put("method", method);

        if (view == null) {
            System.out.println("{\"action\": \"broadcastRefresh\" , \"target\": \"" + view + "\"}");
            socket.emit("callMethod", new JSONObject(payload));
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("activeScreen", view);
            data.put("activateIdleScreen", false);
            payload.put("data", data);
            System.out.println("{\"action\": \"configSaveStartupOptions\" , \"target\": \"" + view + "\"}");
            socket.emit("callMethod", new JSONObject(payload));
        }
    }
}

