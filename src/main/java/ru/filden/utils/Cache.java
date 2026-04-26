package ru.filden.utils;

import ru.filden.impl.Cacheable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache<T> implements Cacheable<Map<Integer, T>> {
    private Map<Integer, T> cache = new ConcurrentHashMap<>();
    private boolean loaded = false;

    @Override
    public void loadCache(Map<Integer, T> data) {
        if (data != null) {
            cache.clear();
            cache.putAll(data);
            loaded = true;
        }
    }
    @Override
    public Map<Integer, T> getFromCache() {
        return cache;
    }

    @Override
    public void clearCache() {
        cache.clear();
        loaded = false;
    }

    @Override
    public boolean isCacheLoaded() {
        return loaded;
    }

    public T getById(Integer id) {
        return cache.get(id);
    }

    public boolean containsKey(Integer id) {
        return cache.containsKey(id);
    }
}
