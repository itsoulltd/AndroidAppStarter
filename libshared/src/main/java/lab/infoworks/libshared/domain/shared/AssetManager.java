package lab.infoworks.libshared.domain.shared;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AssetManager {

    public static List<Map<String, Object>> readJsonObject(Context context, String filename){
        return readJsonObject(readJsonFile(context, filename));
    }

    public static String readJsonFile(Context context, String filename){
        String json = "";
        InputStream is = null;
        try {
            is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return json;
    }

    public static byte[] readAsBytes(InputStream ios) {
        if (ios == null) return new byte[0];
        try {
            byte[] bites = new byte[ios.available()];
            ios.read(bites);
            return bites;
        } catch (IOException e) {
        }
        return new byte[0];
    }

    public static List<Map<String, Object>> readJsonObject(String json){
        if (Message.isValidJson(json)){
            if (json.trim().startsWith("{")){
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map res = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
                    return Arrays.asList(res);
                } catch (IOException e) {}
            }else{
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List res = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
                    return res;
                } catch (IOException e) {}
            }
        }
        return new ArrayList<>();
    }

    public static <T> T readJsonObject(String json, TypeReference<T> typeReference){
        if (Message.isValidJson(json)){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                T res = objectMapper.readValue(json, typeReference);
                return res;
            } catch (IOException e) {}
        }
        return null;
    }

    public static byte[] readImageAsBytes(Bitmap img, Bitmap.CompressFormat format, int quality) throws IOException {
        if (img == null) return new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (quality <= 0) quality = 100;
        img.compress(format, quality, baos);
        byte[] imageBytes = baos.toByteArray();
        return imageBytes;
    }

    public static String readImageAsBase64(Bitmap img, Bitmap.CompressFormat format, int quality) throws IOException {
        byte[] bytes = readImageAsBytes(img, format, quality);
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap readImageFromBase64(String base64DecodedContent) throws IOException {
        byte[] bytes = Base64.decode(base64DecodedContent, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public static Bitmap readAsImage(InputStream ios, int imgType) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(ios);
        return bitmap;
    }

    public static Bitmap createScaledCopyFrom(Bitmap originalImage, int scaled) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        //
        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = scaled;
            height = (int) (width / bitmapRatio);
        } else {
            height = scaled;
            width = (int) (height * bitmapRatio);
        }
        Bitmap image = Bitmap.createScaledBitmap(originalImage, width, height, true);
        return image;
    }

}
