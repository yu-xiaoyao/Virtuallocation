package me.yuxiaoyao.virtuallocation.cache;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class ListHistoryCache<T> {


    private static CacheConverter<String> cacheConverter = new JacksonCacheConverter();

    /**
     * 最大
     */
    private static int LIST_LIMIT_SIZE = 20;

    public static <T> ListHistoryCache<T> with(Context context, String name) {
        return new ListHistoryCache<>(context, name);
    }


    private final Context context;
    private final String name;

    public ListHistoryCache(Context context, String name) {
        this.context = context;
        this.name = name;
    }


    public void putAll(String key, List<T> list) {
        Set<String> sets = objListToSet(list);
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().putStringSet(key, sets).apply();
    }

    public List<T> getAll(String key, Class<T> cls) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Set<String> stringSet = sp.getStringSet(key, Collections.emptySet());
        List<T> list = new ArrayList<>(stringSet.size());
        for (String s : stringSet) {
            T t = cacheConverter.deserialize(s, cls);
            list.add(t);
        }
        return list;
    }

    public <T> void add(String key, T content) {
        String c = cacheConverter.serialize(content);
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Set<String> stringSet = sp.getStringSet(key, null);

        Set<String> save = new LinkedHashSet<>();
        save.add(c);

        if (stringSet != null) {
            int index = 0;
            Iterator<String> iterator = stringSet.iterator();
            while (iterator.hasNext() && index < LIST_LIMIT_SIZE) {
                index++;
                save.add(iterator.next());
            }
        }
        sp.edit().putStringSet(key, save).apply();
    }

    public void removeAll(String key) {
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().remove(key).apply();
    }

    public void remove(String key, int index) {
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Set<String> stringSet = sp.getStringSet(key, null);
        if (stringSet != null && index < stringSet.size()) {
            List<String> strings = new ArrayList<>(stringSet);
            strings.remove(index);
            sp.edit().putStringSet(key, new LinkedHashSet<>(strings)).apply();
        }
    }

    private Set<String> objListToSet(List<T> list) {
        Set<String> set = new LinkedHashSet<>(list.size());

        for (T t : list) {
            if (t instanceof String) {
                set.add((String) t);
            } else {
                String s = cacheConverter.serialize(t);
                set.add(s);
            }
        }

        return set;
    }

}
