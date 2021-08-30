package lab.infoworks.libshared.domain.shared;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoworks.lab.rest.models.Message;

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

}
