package lab.infoworks.libshared.domain.shared;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MediaStorage {

    public static class Builder implements FromUri, Select, Fetch, Where, OrderBy {

        private Type type;
        private Class<?> mediaClsType;
        private Context appContext;

        public Builder(Context application) {
            this.appContext = (application instanceof Application)
                    ? application.getApplicationContext()
                    : application;
        }

        private Uri from;
        private String[] projection;
        private Predicate predicate;
        private Order order;
        private String orderColumn;
        private Consumer onComplete;

        @Override
        public Select from(Type type) {
            this.type = type;
            if (type == Type.Video){
                mediaClsType = MediaStore.Video.class;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    from = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    from = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
            } else if (type == Type.Audio){
                mediaClsType = MediaStore.Audio.class;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    from = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    from = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
            } else if (type == Type.Image){
                mediaClsType = MediaStore.Images.class;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    from = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else {
                    from = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
            } else if (type == Type.File){
                mediaClsType = MediaStore.Files.class;
                from = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
            } else if (type == Type.Download){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    mediaClsType = MediaStore.Downloads.class;
                    //from = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
                    from = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL);
                } else{
                    //Oder not implemented:
                    throw new RuntimeException("MediaStore.Downloads.class not available before API-Level 29");
                }
            }
            return this;
        }

        @Override
        public Where select(String... projections) {
            this.projection = projections;
            return this;
        }

        @Override
        public OrderBy where(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        @Override
        public Fetch orderBy(Order order, String column) {
            this.order = order;
            this.orderColumn = column;
            return this;
        }

        @Override
        public Fetch orderBy(String column) {
            this.order = Order.ASC;
            this.orderColumn = column;
            return this;
        }

        /**
         * Call the fetch() method in a worker thread.
         * @param mapper
         * @param <T>
         * @return
         */
        @Override
        public <T extends MediaStoreItem> List<T> fetch(MediaStoreItemMapper<T> mapper) {
            if (from == null) return new ArrayList<>();
            AtomicInteger incrementer = new AtomicInteger(0);
            List<T> items = new ArrayList<>();
            try(Cursor cursor = appContext.getContentResolver().query(
                    from
                    , projection
                    , getSelection()
                    , getSelectionsArgs()
                    , getOrders())) {
                //...
                while (cursor.moveToNext()){
                    T item = mapper.map(cursor, incrementer.incrementAndGet());
                    if (item != null) items.add(item);
                }
            }
            return items;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public <T extends MediaStoreItem> Fetch onComplete(Consumer<List<T>> consumer) {
            this.onComplete = consumer;
            return this;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public <T extends MediaStoreItem> void fetch(Executor executor, MediaStoreItemMapper<T> mapper) {
            if (executor != null && onComplete != null){
                executor.execute(() -> {
                    List<T> items = fetch(mapper);
                    onComplete.accept(items);
                });
            }
        }

        private String[] getSelectionsArgs() {
            if (predicate != null){
                Expression[] expressions = predicate.resolveExpressions();
                if (expressions != null && expressions.length > 0){
                    List<String> res = new ArrayList<>();
                    for (Expression exp : expressions) {
                        if (exp.getValueProperty().getValue() == null) continue;
                        res.add(exp.getValueProperty().getValue().toString());
                    }
                    return res.toArray(new String[0]);
                }
            }
            return null;
        }

        private String getSelection() {
            if (predicate != null){
                Expression[] expressions = predicate.resolveExpressions();
                if (expressions != null && expressions.length > 0){
                    Expression expression = expressions[0];
                    String pro = expression.interpret();
                    return pro;
                }
            }
            return null;
        }

        private String getOrders() {
            if (orderColumn == null || orderColumn.isEmpty()) return null;
            return orderColumn.replaceAll(" ", "") + " " + order.name();
        }

        private Bundle getQueryArgs(){
            return new Bundle();
        }
    }

    public enum Order {ASC, DESC}
    public enum Type {Video, Audio, Image, File, Download}

    public interface FromUri extends Select {
        Select from(Type type);
    }

    public interface Select extends Where {
        Where select(String...projections);
    }

    public interface Where extends OrderBy {
        OrderBy where(Predicate predicate);
    }

    public interface OrderBy extends Fetch {
        Fetch orderBy(Order order, String column);
        Fetch orderBy(String column);
    }

    public interface Fetch {
        /**
         * Call the fetch() method in a worker thread.
         * @param mapper
         * @param <T>
         * @return
         */
        <T extends MediaStoreItem> List<T> fetch(MediaStoreItemMapper<T> mapper);

        <T extends MediaStoreItem> void fetch(Executor executor, MediaStoreItemMapper<T> mapper);
        <T extends MediaStoreItem> Fetch onComplete(Consumer<List<T>> consumer);
    }

    public static class MediaStoreItem extends Entity {

        private final Uri uri;
        private final String name;
        private final int size;
        private final Type type;

        public MediaStoreItem(Type type, Uri uri, String name, int size) {
            this.type = type;
            this.uri = uri;
            this.name = name;
            this.size = size;
        }

        public Uri getUri() {
            return uri;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public Type getType() {
            return type;
        }
    }

    @FunctionalInterface
    public interface MediaStoreItemMapper<Mapper extends MediaStoreItem> {
        Mapper map(Cursor cursor, int index);
    }

}
