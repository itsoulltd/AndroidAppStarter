package lab.infoworks.libshared.domain.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.infoworks.lab.rest.models.Message;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import static java.lang.Integer.getInteger;

public class AppStorage {

    public static AppStorage getCurrent(@NotNull Context activity) {
        return new AppStorage(activity);
    }

    public AppStorage(@NotNull Context application) {
        currentPreference = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    private static final String PREFERENCES_FILE_NAME = AppStorage.class.getSimpleName() + "Preferences";
    private SharedPreferences currentPreference;

    public void put(String key, Object value) {

        if (currentPreference == null)
            return;

        if (value == null) {
            remove(key);
            return;
        }

        SharedPreferences.Editor editor = currentPreference.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
            editor.apply();
        } else if (value instanceof Number) {
            Number val = ((Number) value);
            editor.putString(key, val.toString());
            editor.apply();
        } else if (value instanceof Boolean) {
            Boolean val = ((Boolean) value);
            editor.putString(key, String.valueOf(val));
            editor.apply();
        } else {
            String json = marshalMessagePayload(value);
            if (json != null) {
                editor.putString(key, json);
                editor.apply();
            }
        }
        //
    }

    protected <P extends Object> String marshalMessagePayload(P object) {
        try {
            return Message.marshal(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <P extends Object> P unmarshalMessagePayload(Object type, String payload) {
        if (Message.isValidJson(payload)) {
            Object obj = null;
            try {
                if (type instanceof TypeReference) {
                    obj = Message.unmarshal((TypeReference)type, payload);
                } else {
                    obj = Message.unmarshal((Class<P>) type, payload);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (P) obj;
        }
        return null;
    }

    public <P extends Object> P objectValue(String key, Class<P> type) {
        if (currentPreference == null) return null;
        return unmarshalMessagePayload(type, currentPreference.getString(key, null));
    }

    public <P extends Object> P objectValue(String key, TypeReference type) {
        if (currentPreference == null) return null;
        return unmarshalMessagePayload(type, currentPreference.getString(key, null));
    }

    public String stringValue(String key) {

        if (currentPreference == null)
            return null;

        return currentPreference.getString(key, null);
    }

    public long longValue(String key) {

        if (currentPreference == null)
            return 0l;

        String val = currentPreference.getString(key, null);
        if (val != null)
            return Long.getLong(val);

        return 0l;
    }

    public int intValue(String key) {

        if (currentPreference == null)
            return 0;

        String val = currentPreference.getString(key, null);
        if (val != null)
            return getInteger(val);

        return 0;
    }

    public double doubleValue(String key) {

        if (currentPreference == null)
            return 0;

        String val = currentPreference.getString(key, null);
        if (val != null)
            return Double.parseDouble(val);

        return 0;
    }

    public boolean boolValue(String key) {

        if (currentPreference == null)
            return false;

        String val = currentPreference.getString(key, null);
        if (val != null)
            return Boolean.getBoolean(val);

        return false;
    }

    public void remove(String key) {
        if (currentPreference == null)
            return;

        SharedPreferences.Editor editor = currentPreference.edit();
        editor.remove(key);
        editor.apply();
    }

    public boolean synchronousRemove(String key) {
        if (currentPreference == null)
            return false;

        SharedPreferences.Editor editor = currentPreference.edit();
        editor.remove(key);
        return editor.commit();
    }

    public void clear() {
        if (currentPreference == null)
            return;

        SharedPreferences.Editor editor = currentPreference.edit();
        editor.clear();
        editor.apply();
    }

    public boolean synchronousClear() {
        if (currentPreference == null)
            return false;

        SharedPreferences.Editor editor = currentPreference.edit();
        editor.clear();
        return editor.commit();
    }
}
