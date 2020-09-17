package lab.infoworks.libshared.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationCenter {

    private static Map<String, List<BroadcastReceiver>> contextMapper = new ConcurrentHashMap<>();

    private static String getKey(Context context, String notifications){
        String key = context.getClass().getName() + notifications;
        return key;
    }

    private static List<BroadcastReceiver> getReceivers(String key){
        List<BroadcastReceiver> items = contextMapper.get(key);
        if(items == null){
            items = new ArrayList<>();
            contextMapper.put(key, items);
        }
        return items;
    }

    private static void putIntoMapper(Context context, String notification, BroadcastReceiver responseHandler) {
        //
        List<BroadcastReceiver> receivers = getReceivers(getKey(context, notification));
        receivers.add(responseHandler);
    }

    private static boolean checkParams(Context context, String notifications){
        if (context == null) return false;
        if (notifications == null || notifications.isEmpty()) return false;
        return true;
    }

    public static void addObserver(Context context, String notification, BroadcastReceiver responseHandler) {
        if (!checkParams(context, notification)) return;
        putIntoMapper(context, notification, responseHandler);
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(responseHandler
                        , new IntentFilter(notification));
    }

    public static void addObserver(Context context, String notification, NotificationHandler responseHandler) {
        if (!checkParams(context, notification)) return;
        SimpleBroadcastReceiver receiver = new SimpleBroadcastReceiver(responseHandler);
        addObserver(context, notification, receiver);
    }

    public static void addObserverOnMain(Context context, String notification, NotificationHandler responseHandler) {
        if (!checkParams(context, notification)) return;
        SimpleBroadcastReceiver receiver = new SimpleBroadcastReceiver(responseHandler, true);
        addObserver(context, notification, receiver);
    }

    protected static void removeObserver(Context context, BroadcastReceiver responseHandler) {
        if (context == null) return;
        if (responseHandler != null)
            LocalBroadcastManager.getInstance(context).unregisterReceiver(responseHandler);
        else {
            Log.e("NotificationCenter", "removeObserver: unregisterReceiver(responseHandler)! Might lead to leaks");
        }
    }

    protected static void removeThemAll(Context context, String notifications){
        List<BroadcastReceiver> all = getReceivers(getKey(context, notifications));
        for (BroadcastReceiver broadcastReceiver : all) {
            removeObserver(context, broadcastReceiver);
        }
    }

    public static void removeObserver(Context context, String notification) {
        if (!checkParams(context, notification)) return;
        removeThemAll(context, notification);
    }

    public static void postNotification(Context context, String notification, Map<String, Object> params) {
        if (!checkParams(context, notification)) return;
        Intent intent = new Intent(notification);
        // insert parameters if needed
        if (params != null && !params.isEmpty()) {
            for(Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null)
                    intent.putExtra(key, value.toString());
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private final static class SimpleBroadcastReceiver extends BroadcastReceiver {

        private NotificationHandler handler;
        private boolean dispatchOnMain;

        public SimpleBroadcastReceiver(NotificationHandler handler) {
            this(handler, false);
        }

        public SimpleBroadcastReceiver(NotificationHandler handler, boolean dispatchOnMain) {
            if (handler == null){
                handler = (context, intent) -> {
                    Log.e("SimpleBroadcastReceiver"
                        , "NotificationHandler Not Provided! Please Check"
                        , new Exception("NotificationHandler Not Provided! Exception"));
                };
            }
            this.handler = handler;
            this.dispatchOnMain = dispatchOnMain;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (handler != null){
                if (dispatchOnMain){
                    new Handler().post(() -> {
                        handler.apply(context, intent);
                    });
                }else {
                    handler.apply(context, intent);
                }
            }
        }
    }

    @FunctionalInterface
    public interface NotificationHandler {
        void apply(Context context, Intent intent);
    }

}
