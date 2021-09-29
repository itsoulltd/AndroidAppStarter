package lab.infoworks.libshared.domain.shared;

import android.app.Application;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class FileManager implements AutoCloseable{

    public static final String LOG_TAG = FileManager.class.getSimpleName();

    @Override
    public void close() throws Exception {
        if (executor != null && !executor.isShutdown()){
            executor.shutdown();
            executor = null;
        }
    }

    public enum StorageMode{
        INTERNAL, EXTERNAL;
    }

    private Context appContext;
    private StorageMode mode = StorageMode.INTERNAL;
    private ExecutorService executor;

    public ExecutorService getExecutor() {
        if (executor == null){
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    public Context getAppContext() {
        return appContext;
    }

    public FileManager(Context application) {
        this.appContext = (application instanceof Application)
                ? application.getApplicationContext()
                : application;
    }

    public FileManager(Context application, StorageMode mode) {
        this(application);
        this.mode = mode;
    }

    /**
     *
     * @param folderName
     * @return
     */
    public File createFolder(String folderName) {
        if (mode == StorageMode.INTERNAL){
            File root = getAppContext().getFilesDir();
            final File folder = new File(root, folderName);
            if (folder == null || !folder.mkdirs()) {
                Log.e(LOG_TAG, "Directory not created");
            }
            return folder;
        } else {
            return (isExternalStorageWritable())
                    ? createAppSpecificExternalFolder(folderName, Environment.DIRECTORY_DOCUMENTS)
                    : null;
        }
    }

    /**
     *
     * @param folderName
     * @param environmentDirectory e.g. Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_DOCUMENTS etc
     * @return
     */
    public File createAppSpecificExternalFolder(String folderName, String environmentDirectory) {
        if (!isExternalStorageWritable()) return null;
        File file = new File(getAppContext().getExternalFilesDir(environmentDirectory), folderName);
        if (file == null || !file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public File getFile(File folder, String fileName, boolean removeIfExist){
        File file = new File(folder, fileName);
        if (removeIfExist && file.exists()) file.delete();
        return file;
    }

    public void saveBitmap(Bitmap bitmap, File folder, String fileName, int quality) throws IOException {
        File imgFile = getFile(folder, fileName, true);
        try (FileOutputStream fos = new FileOutputStream(imgFile)){
            Bitmap.CompressFormat format = (fileName.toLowerCase().contains("png"))
                    ? Bitmap.CompressFormat.PNG
                    : Bitmap.CompressFormat.JPEG;
            bitmap.compress(format, quality, fos);
            fos.flush();
        }
    }

    public void asyncSaveBitmap(Bitmap bitmap, File folder, String fileName, int quality) {
        getExecutor().submit(() -> {
            try {
                saveBitmap(bitmap, folder, fileName, quality);
            } catch (IOException e) { Log.d(LOG_TAG, "availableBytes: " + e.getMessage()); }
        });
    }

    public Bitmap readBitmap(File folder, String fileName) throws IOException {
        File imgFile = getFile(folder, fileName, false);
        if (imgFile.exists()){
            try(FileInputStream fos = new FileInputStream(imgFile)) {
                return AssetManager.readAsImage(fos, 0);
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readBitmap(File folder, String fileName, Consumer<Bitmap> consumer) {
        getExecutor().submit(() -> {
            try {
                if (consumer != null){
                    Bitmap bitmap = readBitmap(folder, fileName);
                    consumer.accept(bitmap);
                }
            }catch (IOException e) {Log.d(LOG_TAG, "availableBytes: " + e.getMessage());}
        });
    }

    public long availableBytes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StorageManager storageManager = getAppContext().getSystemService(StorageManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                StorageStatsManager stManager = getAppContext().getSystemService(StorageStatsManager.class);
                if (mode == StorageMode.INTERNAL) {
                    try {
                        UUID uuid = storageManager.getUuidForPath(getAppContext().getFilesDir());
                        return stManager.getFreeBytes(uuid);
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "availableBytes: " + e.getMessage());
                    }
                } else {
                    try{
                        UUID uuid = storageManager.getUuidForPath(getAppContext().getExternalFilesDir(null));
                        return stManager.getFreeBytes(uuid);
                    }catch (IOException e) {Log.d(LOG_TAG, "availableBytes: " + e.getMessage());}
                }
            }
        }
        return 0l;
    }

    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

}
