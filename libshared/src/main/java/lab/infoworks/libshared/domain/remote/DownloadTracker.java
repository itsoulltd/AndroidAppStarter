package lab.infoworks.libshared.domain.remote;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.it.soul.lab.sql.entity.Entity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class DownloadTracker {

    public static final String TAG = "DownloadTracker";
    private static Map<Long, TrackItem> sourceMap = new ConcurrentHashMap<>();

    public static void viewOnGoingDownloads(Activity activity){
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        activity.startActivity(intent);
    }

    public static void registerReceiverForCompletion(Context application){
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        application.registerReceiver(receiver, filter);
    }

    private static BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            TrackItem item = sourceMap.remove(referenceId);
            if (item != null){
                try {
                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    ParcelFileDescriptor fileDescriptor = manager.openDownloadedFile(referenceId);
                    try (FileInputStream ios = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor)){
                        if (item.getConsumer() != null) {
                            item.getConsumer().accept(ios);
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "onReceive: " + e.getMessage());
                    }
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "onReceive: " + e.getMessage());
                }
            }
        }
    };

    public static class Builder implements Tracker, DownloadRequest{

        private final TrackItem item;
        private final DownloadManager.Request request;
        private final DownloadManager manager;
        private WeakReference<Context> weakContext;

        public Builder(Context context) {
            item = null;
            request = null;
            manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            weakContext = new WeakReference<>(context);
        }

        public Builder(Context context, String downloadLink) {
            item = new TrackItem(downloadLink);
            request = new DownloadManager.Request(item.link);
            manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            weakContext = new WeakReference<>(context);
        }

        /**
         * Do not close FileInputStream your self. this will taken care by the calling method.
         * @param consumer
         */
        @Override
        public long enqueue(Consumer<FileInputStream> consumer) {
            if (consumer == null) return 0l;
            Long ref = manager.enqueue(request);
            item.setRef(ref);
            item.setRequest(request);
            item.setConsumer(consumer);
            sourceMap.put(item.getRef(), item);
            return ref;
        }

        @Override
        public void checkStatus(long ref, Executor executor, Consumer<TrackItemStatus> consumer) {
            if (consumer == null) return;
            executor.execute(() -> {
                TrackItemStatus status = checkStatus(ref);
                consumer.accept(status);
            });
        }

        @Override
        public TrackItemStatus checkStatus(long ref) {
            TrackItem fromSource = sourceMap.get(ref);
            if (fromSource != null){
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(fromSource.getRef());
                Cursor cursor = manager.query(query);
                if(cursor.moveToFirst()){
                    //column for status
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    //column for reason code if the download failed or paused
                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);
                    //
                    TrackItemStatus itemStatus = new TrackItemStatus(status, reason);
                    return itemStatus;
                }
            }
            return new TrackItemStatus(DownloadManager.STATUS_SUCCESSFUL);
        }

        @Override
        public TrackItemStatus cancel(long ref) {
            TrackItem fromSource = sourceMap.remove(ref);
            if (fromSource != null){
                int status = manager.remove(fromSource.getRef());
                return new TrackItemStatus(status);
            }
            return new TrackItemStatus(DownloadManager.STATUS_SUCCESSFUL);
        }

        /**
         * e.g. pass as -> DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE
         * @param types
         * @return
         */
        @Override
        public DownloadRequest setAllowedNetworkTypes(int types) {
            if (request == null) return this;
            request.setAllowedNetworkTypes(types);
            return this;
        }

        @Override
        public DownloadRequest setAllowedOverRoaming(boolean shouldRoaming) {
            if (request == null) return this;
            request.setAllowedOverRoaming(shouldRoaming);
            return this;
        }

        @Override
        public DownloadRequest setTitle(String title) {
            if (request == null) return this;
            request.setTitle(title);
            return this;
        }

        @Override
        public DownloadRequest setDescription(String des) {
            if (request == null) return this;
            request.setDescription(des);
            return this;
        }

        /**
         * @param fileName
         * @param directoryName
         * e.g. Environment.DIRECTORY_DOWNLOADS,
         *          Environment.DIRECTORY_DOCUMENTS,
         *          Environment.DIRECTORY_MOVIES,
         *          Environment.DIRECTORY_PICTURES,
         *          Environment.DIRECTORY_MUSIC
         * @return
         */
        @Override
        public DownloadRequest setDestinationInExternalFilesDir(String fileName, String directoryName) {
            if (request == null) return this;
            request.setDestinationInExternalFilesDir(weakContext.get(), directoryName, fileName);
            return this;
        }

        @Override
        public DownloadRequest setNotificationVisibility(int visibility) {
            if (request == null) return this;
            request.setNotificationVisibility(visibility);
            return this;
        }
    }

    public interface Tracker {
        long enqueue(Consumer<FileInputStream> consumer);
        void checkStatus(long ref, Executor executor, Consumer<TrackItemStatus> consumer);
        TrackItemStatus checkStatus(long ref);
        TrackItemStatus cancel(long ref);
    }

    public interface DownloadRequest extends Tracker{
        DownloadRequest setAllowedNetworkTypes(int types);
        DownloadRequest setAllowedOverRoaming(boolean shouldRoaming);
        DownloadRequest setTitle(String title);
        DownloadRequest setDescription(String des);
        DownloadRequest setDestinationInExternalFilesDir(String fileName, String directoryName);
        DownloadRequest setNotificationVisibility(int visibility);
    }

    private static class TrackItem extends Entity{

        static Integer getHashFrom(String linkStr) {
            return Objects.hash(linkStr);
        }

        private Uri link;
        private String linkStr;
        private Long ref;
        private DownloadManager.Request request;
        private Consumer<FileInputStream> consumer;

        public TrackItem(String linkStr) {
            this.linkStr = linkStr;
            link = Uri.parse(linkStr);
        }

        public Uri getLink() {
            return link;
        }

        public void setLink(Uri link) {
            this.link = link;
        }

        public String getLinkStr() {
            return linkStr;
        }

        public void setLinkStr(String linkStr) {
            this.linkStr = linkStr;
        }

        public Long getKey() {
            return ref;
        }

        public Long getRef() {
            return ref;
        }

        public void setRef(Long ref) {
            this.ref = ref;
        }

        public DownloadManager.Request getRequest() {
            return request;
        }

        public void setRequest(DownloadManager.Request request) {
            this.request = request;
        }

        public Consumer<FileInputStream> getConsumer() {
            return consumer;
        }

        public void setConsumer(Consumer<FileInputStream> consumer) {
            this.consumer = consumer;
        }
    }

    public static class TrackItemStatus extends Entity{
        private String status;
        private String reason;
        private String downloadedFileName;

        public TrackItemStatus(int status){
            this.status = translateStatus(status);
        }

        public TrackItemStatus(int status, int reason){
            this(status);
            this.reason = translateReason(reason);
        }

        public TrackItemStatus(int status, int reason, String downloadedFileName) {
            this(status, reason);
            this.downloadedFileName = downloadedFileName;
        }

        private String translateStatus(int status) {
            String statusText = "";
            switch (status){
                case DownloadManager.STATUS_SUCCESSFUL:
                    statusText = "STATUS_SUCCESSFUL";
                    break;
                case DownloadManager.STATUS_FAILED:
                    statusText = "STATUS_FAILED";
                    break;
                case DownloadManager.STATUS_PAUSED:
                    statusText = "STATUS_PAUSED";
                    break;
                case DownloadManager.STATUS_PENDING:
                    statusText = "STATUS_PENDING";
                    break;
                default:
                    statusText = "STATUS_RUNNING";
            }
            return statusText;
        }

        private String translateReason(int reason) {
            String reasonText = "";
            switch(reason) {
                case DownloadManager.ERROR_CANNOT_RESUME:
                    reasonText = "ERROR_CANNOT_RESUME";
                    break;
                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                    reasonText = "ERROR_DEVICE_NOT_FOUND";
                    break;
                case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                    reasonText = "ERROR_FILE_ALREADY_EXISTS";
                    break;
                case DownloadManager.ERROR_FILE_ERROR:
                    reasonText = "ERROR_FILE_ERROR";
                    break;
                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                    reasonText = "ERROR_HTTP_DATA_ERROR";
                    break;
                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                    reasonText = "ERROR_INSUFFICIENT_SPACE";
                    break;
                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                    reasonText = "ERROR_TOO_MANY_REDIRECTS";
                    break;
                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                    reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                    break;
                case DownloadManager.ERROR_UNKNOWN:
                    reasonText = "ERROR_UNKNOWN";
                    break;
                case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                    reasonText = "PAUSED_QUEUED_FOR_WIFI";
                    break;
                case DownloadManager.PAUSED_UNKNOWN:
                    reasonText = "PAUSED_UNKNOWN";
                    break;
                case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                    reasonText = "PAUSED_WAITING_FOR_NETWORK";
                    break;
                case DownloadManager.PAUSED_WAITING_TO_RETRY:
                    reasonText = "PAUSED_WAITING_TO_RETRY";
                    break;
            }
            return reasonText;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDownloadedFileName() {
            return downloadedFileName;
        }

        public void setDownloadedFileName(String downloadedFileName) {
            this.downloadedFileName = downloadedFileName;
        }
    }
}
