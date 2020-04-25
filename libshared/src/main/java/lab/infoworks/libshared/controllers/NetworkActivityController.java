package lab.infoworks.libshared.controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lab.infoworks.libshared.BuildConfig;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkActivityController {

    public interface NetworkMonitoring {
        void openNetworkErrorBottomSheet(String message);
        void closeNetworkErrorBottomSheet();
    }

    private static final String TAG = NetworkActivityController.class.getSimpleName();

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private WeakReference<NetworkMonitoring> networkMonitoring;

    public void onDestroy(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disableNetworkConnectivity();
        }
    }

    public void monitorNetwork(Context context, boolean enableConnectivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (enableConnectivity){
                enableNetworkConnectivity(context);
            }else {
                disableNetworkConnectivity();
            }
        }
        if (NetworkMonitoring.class.isAssignableFrom(context.getClass())) {
            setNetworkMonitoring((NetworkMonitoring) context);
        }
    }

    public void setNetworkMonitoring(NetworkMonitoring context) {
        networkMonitoring = new WeakReference<>(context);
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    private void enableNetworkConnectivity(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                networkCallback = getNetworkCallBack();
                //connectivityManager.registerDefaultNetworkCallback(networkCallback);
                NetworkRequest request = new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build();
                connectivityManager.registerNetworkCallback(request, networkCallback);
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    public void disableNetworkConnectivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                networkMonitoring.clear();
                connectivityManager = null;
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private ConnectivityManager.NetworkCallback getNetworkCallBack() {
        /*final boolean isNetworkMetered = (connectivityManager != null) ? connectivityManager.isActiveNetworkMetered()
                : false;
          //Log.d(TAG, "connected to " + (isNetworkMetered ? "LTE" : "WIFI"));
                */
        return new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                //Log.d(TAG, "onAvailable");
                checkNetworkAvailability();
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                //Log.d(TAG, "onLost");
                checkNetworkAvailability();
            }

            @Override
            public void onLosing(@NonNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
                //Log.d(TAG, "onLosing");
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                //Log.d(TAG, "onUnavailable");
            }
        };
    }

    /**
     * Call from main thread
     */
    public final void checkNetworkAvailability() {
        NetworkActivityController.isNetworkAvailable(new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (networkMonitoring.get() == null) return;
                boolean isConnected = (msg.obj instanceof Boolean) ? ((Boolean)msg.obj).booleanValue() : false;
                if (!isConnected){
                    String message = "Network unavailable";
                    networkMonitoring.get().openNetworkErrorBottomSheet(message);
                } else {
                    networkMonitoring.get().closeNetworkErrorBottomSheet();
                }
                Log.d(TAG, "Network Available: " + ((isConnected) ? "YES" : "NO"));
            }
        });
    }

    private final static InetSocketMonitor monitor = new InetSocketMonitor();

    public static void isNetworkAvailable(Handler handler) {
        if (BuildConfig.DEBUG){
            monitor.setMaxTry(7);
            monitor.setDelayBWTry(600l);
        }
        monitor.isNetworkAvailable(handler);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public static boolean isNetworkAvailable(Context context) {
        //Need To Test:
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            isConnected = (activeNetwork != null);
        }

        return isConnected;
    }

    ////////////////////////////////////////////////////////////////////////////

    private static class InetSocketMonitor{

        private ExecutorService executor = Executors.newSingleThreadExecutor();
        private Future future;
        private final String ADDRESS;
        private final int PORT;
        private final int TIMEOUT_MS;
        private int maxTry = 3;
        private long delayBWTry = 1000l;

        public InetSocketMonitor() {
            /**
             * Discussion On StackOverflow about fastest approach to check internet:
             * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
             */
            this("8.8.8.8", 53, 1000);
        }

        public InetSocketMonitor(String ADDRESS, int PORT, int TIMEOUT_MS) {
            this.ADDRESS = ADDRESS;
            this.PORT = PORT;
            this.TIMEOUT_MS = TIMEOUT_MS;
        }

        public void isNetworkAvailable(Handler handler) {
            //
            if (future != null && !future.isDone()) return;
            future = executor.submit(() -> {
                Message msg = new Message();
                msg.obj = false;
                int failedAttempt = 0;
                int tryCount = 0;
                while (tryCount < maxTry){
                    try {
                        Socket sock = new Socket();
                        SocketAddress sockaddr = new InetSocketAddress(ADDRESS, PORT);
                        // This will block no more than TIMEOUT_MS
                        sock.connect(sockaddr, TIMEOUT_MS);
                        sock.close();
                        break; //On a single successful attempt, we break to notify net availability.
                    } catch (IOException e) {
                        failedAttempt++;
                        Log.d(TAG, String.format("TryCount %s on Failed, reason: %s", failedAttempt, e.getMessage()));
                    }
                    try {
                        Thread.sleep(delayBWTry);
                    } catch (InterruptedException e) {}
                    tryCount++;
                }
                //Net is not available, when failedAttempt is greater then the half of MaxTry:
                msg.obj = !(failedAttempt > (maxTry / 2));
                handler.sendMessage(msg);
            });
        }

        public void setMaxTry(int maxTry) {
            if (maxTry <= 0) return;
            this.maxTry = maxTry;
        }

        public void setDelayBWTry(long delayBWTry) {
            if (delayBWTry <= 0) return;
            this.delayBWTry = delayBWTry;
        }
    }

}
