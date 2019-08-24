package lab.itsoul.com.deliman.rider.startup.controllers;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkManager {
    private static Executor executor = Executors.newSingleThreadExecutor();
    private final static String ADDRESS = "8.8.8.8";
    private final static int PORT = 53;
    private final static int TIMEOUT_MS = 1000;

    public static void isNetworkAvailable(Handler handler) {
        Message msg = new Message();
        executor.execute(() -> {
            try {
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress(ADDRESS, PORT);

                sock.connect(sockaddr, TIMEOUT_MS); // This will block no more than TIMEOUT_MS
                sock.close();
                msg.obj = true;
            } catch (IOException e) { msg.obj = "Network unavailable"; }
            finally {
                if(msg.obj == null) msg.obj = "An error occurred!";
                handler.sendMessage(msg);
            }
        });
    }
}
