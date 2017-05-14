package com.zwb.commonlibs.utils;

import android.support.v4.util.LruCache;

/**
 * 内存缓存，使用LruCache实现
 *
 * @author zwb
 */
public class MemCacheManager {
    private static LruCache<String, Object> mCache = new LruCache<>(10);

    /**
     * 获取缓存
     *
     * @param key 缓存标记
     * @return 获取到的缓存，若不存在或被gc则返回null
     */
    public static <T> T get(String key) {
        //noinspection unchecked
        return (T) mCache.get(key);
    }

    /**
     * 写入缓存
     *
     * @param key   缓存标记
     * @param value 缓存的值
     */
    public static <T> void put(String key, T value) {
        mCache.put(key, value);
    }

    /**
     * 清除某一条缓存
     *
     * @param key 缓存标记
     */
    public static void remove(String key) {
        mCache.remove(key);
    }

    public static void clear(){
        mCache.evictAll();
    }
}
