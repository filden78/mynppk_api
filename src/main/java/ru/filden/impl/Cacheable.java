package ru.filden.impl;

public interface Cacheable<T> {
    void loadCache(T data);
    T getFromCache();
    void clearCache();
    boolean isCacheLoaded();
}
