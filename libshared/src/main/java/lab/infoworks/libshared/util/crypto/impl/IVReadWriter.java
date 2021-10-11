package lab.infoworks.libshared.util.crypto.impl;

import android.content.Context;
import android.util.Base64;

import lab.infoworks.libshared.domain.shared.AppStorage;
import lab.infoworks.libshared.util.crypto.definition.IVectorIO;

public class IVReadWriter implements IVectorIO {

    private AppStorage storage;

    public IVReadWriter(Context context) {
        this.storage = AppStorage.getCurrent(context);
    }

    private String extendAlias(String alias){
        return alias + "_" + IVReadWriter.class.getSimpleName();
    }

    @Override
    public boolean isSaved(String alias) {
        String val = storage.stringValue(extendAlias(alias));
        return (val != null && !val.isEmpty()) ? true : false;
    }

    @Override
    public byte[] read(String alias) {
        String val = storage.stringValue(extendAlias(alias));
        if (val != null && !val.isEmpty()){
            byte[] bytes = Base64.decode(val, Base64.DEFAULT);
            return bytes;
        }
        return new byte[0];
    }

    @Override
    public void write(String alias, byte[] bytes) {
        String val = Base64.encodeToString(bytes, Base64.DEFAULT);
        storage.put(extendAlias(alias), val);
    }
}
