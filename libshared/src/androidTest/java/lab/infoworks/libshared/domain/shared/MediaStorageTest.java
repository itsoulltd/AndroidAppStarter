package lab.infoworks.libshared.domain.shared;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MediaStorageTest {

    private Context appContext;

    @Rule
    public GrantPermissionRule storageReadRule = GrantPermissionRule.grant("android.permission.READ_EXTERNAL_STORAGE");
    @Rule
    public GrantPermissionRule storageWriteRule = GrantPermissionRule.grant("android.permission.WRITE_EXTERNAL_STORAGE");

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void stockFlowTest(){

        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.
        Assert.assertTrue("READ Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);
        Assert.assertTrue("WRITE Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);

        List<Video> videoList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        String selection = MediaStore.Video.Media.DURATION + " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
        };
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
        //
        try (Cursor cursor = appContext.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                videoList.add(new Video(contentUri, name, duration, size));
            }
        }
        //
    }

    @Test
    public void createFlowTest() {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.
        Assert.assertTrue("READ Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);
        Assert.assertTrue("WRITE Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);

        //Write the search clause:
        String durationIs = String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
        Predicate predicate = new Where(MediaStore.Video.Media.DURATION)
                .isGreaterThenOrEqual(durationIs);
        //Fetch the query:
        List<MediaStorage.MediaStoreItem> items = new MediaStorage.Builder(appContext)
                .from(MediaStorage.Type.Video)
                .select(MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE)
                .where(predicate)
                .orderBy(MediaStore.Video.Media.DISPLAY_NAME)
                .fetch((cursor, index) -> {
                    //Get values of columns for a given video.
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    //
                    int nameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    String name = cursor.getString(nameColumn);
                    //
                    int durationColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                    int duration = cursor.getInt(durationColumn);
                    //
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                    int size = cursor.getInt(sizeColumn);
                    //Finally return:
                    return new Video(contentUri, name, duration, size);
                });
        //Do what you want to do with items
        //...
    }

    @Test
    public void createFlowTest_2() {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.
        Assert.assertTrue("READ Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);
        Assert.assertTrue("WRITE Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);

        //Write the search clause:
        String durationIs = String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
        Predicate predicate = new Where(MediaStore.Video.Media.DURATION)
                .isGreaterThenOrEqual(durationIs);
        //Fetch the query:
        new MediaStorage.Builder(appContext)
                .from(MediaStorage.Type.Video)
                .select(MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE)
                .where(predicate)
                .orderBy(MediaStore.Video.Media.DISPLAY_NAME)
                .onComplete((items) -> {
                    if (items != null){
                        //Do what you want to do with items
                        //Update ui on main thread:
                        /*((Activity) myActivity).runOnUiThread(() -> {
                            //On Main Thread:
                        });*/
                        //...
                    }
                })
                .fetch(Executors.newSingleThreadExecutor(), (cursor, index) -> {
                    //Get values of columns for a given video.
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                    //
                    int nameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    String name = cursor.getString(nameColumn);
                    //
                    int durationColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                    int duration = cursor.getInt(durationColumn);
                    //
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                    int size = cursor.getInt(sizeColumn);
                    //Finally return:
                    return new Video(contentUri, name, duration, size);
                });
        //
    }

    @Test
    public void createFlowTest_Images() {
        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.
        Assert.assertTrue("READ Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);
        Assert.assertTrue("WRITE Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED);

        //Write the search clause:
        String sizeIs = "30";
        Predicate predicate = new Where(MediaStore.Images.Media.SIZE)
                .isGreaterThenOrEqual(sizeIs);
        //Fetch the query:
        List<MediaStorage.MediaStoreItem> items = new MediaStorage.Builder(appContext)
                .from(MediaStorage.Type.Image)
                .select(MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE)
                .where(predicate)
                .orderBy(MediaStore.Images.Media.DISPLAY_NAME)
                .fetch((cursor, index) -> {
                    //Get values of columns for a given video.
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    //
                    int nameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    String name = cursor.getString(nameColumn);
                    //
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                    int size = cursor.getInt(sizeColumn);
                    //Finally return:
                    return new Image(contentUri, name, size);
                });
        //Do what you want to do with items
        //...
    }

    private class Video extends MediaStorage.MediaStoreItem {

        private final Uri uri;
        private final String name;
        private final int duration;
        private final int size;

        public Video(Uri uri, String name, int duration, int size) {
            super(MediaStorage.Type.Video, uri, name, size);
            this.uri = uri;
            this.name = name;
            this.duration = duration;
            this.size = size;
        }
    }

    private class Image extends MediaStorage.MediaStoreItem {
        private final Uri uri;
        private final String name;
        private final int size;
        public Image(Uri contentUri, String name, int size) {
            super(MediaStorage.Type.Image, contentUri, name, size);
            this.uri = contentUri;
            this.name = name;
            this.size = size;
        }
    }

}