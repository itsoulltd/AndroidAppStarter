package lab.infoworks.libshared.domain.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Response;

import java.io.IOException;
import java.util.Map;

public class ResponseExt extends Response {

    public ResponseExt() {}

    public ResponseExt(String json) {
        if (isValidJson(json)){
            try {
                Map<String, Object> data = unmarshal(new TypeReference<Map<String, Object>>() {}, json);
                unmarshallingFromMap(data, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
