package com.novoda.pxfetcher;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.novoda.imageloader.core.cache.CacheManager;

public class BetterMemoryCache implements CacheManager {

    private static final int MAX_CACHE_SIZE = 16 * 1024 * 1024;
    private LruCache<String, Bitmap> cache;
    private int capacity;

    public BetterMemoryCache(int percentageOfMemoryForCache) {
        this.capacity = calculateCacheSize(percentageOfMemoryForCache);
        reset();
    }

    public int calculateCacheSize(int percentageOfMemoryForCache) {
        Runtime runtime = Runtime.getRuntime();
        int calculatedSize = (int) (runtime.maxMemory() * percentageOfMemoryForCache / 100);
        int cacheSize = Math.min(calculatedSize, MAX_CACHE_SIZE);
        return cacheSize;
    }

    private void reset() {
        if (cache != null) {
            cache.evictAll();
        } else {
            cache = new LruCache<String, Bitmap>(capacity) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return 4 * bitmap.getWidth() * bitmap.getHeight();
                }
            };
        }
    }

    @Override
    public Bitmap get(String url, int width, int height) {
        return cache.get(url);
    }

    @Override
    public void put(String url, Bitmap bmp) {
        cache.put(url, bmp);
    }

    @Override
    public void remove(String url) {
        cache.remove(url);
    }

    @Override
    public void clean() {
        reset();
    }

}
