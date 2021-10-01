package lab.infoworks.libshared.domain.remote;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import lab.infoworks.libshared.domain.shared.AssetManager;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DownloadTrackerTest {

    private Context appContext;

    @Rule
    public GrantPermissionRule internetPermissionRule = GrantPermissionRule.grant("android.permission.INTERNET");

    @Before
    public void setUp() throws Exception {
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void downloadTest(){
        Assert.assertTrue("Internet Permission not Granted!"
                , appContext.checkSelfPermission("android.permission.INTERNET") == PackageManager.PERMISSION_GRANTED);
        //
        CountDownLatch letch = new CountDownLatch(1);
        //
        String link = "https://upload.wikimedia.org/wikipedia/commons/c/c6/A_modern_Cricket_bat_%28back_view%29.jpg";
        DownloadTracker.registerReceiverForCompletion(appContext);
        //
        new DownloadTracker.Builder(appContext, link)
                .setDestinationInExternalFilesDir("myImg.jpg", Environment.DIRECTORY_DOWNLOADS)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                .setTitle("myImg download")
                .enqueue((ios) -> {
                    //Now do whatever you want:
                    try {
                        Bitmap img = AssetManager.readAsImage(ios, 0);
                        System.out.println("");
                        letch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("");
                });

        try {
            letch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}