package com.springframework.cglib.core.internal;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class LoadingCache<K, KK, V> {
    protected final ConcurrentMap<KK, Object> map;
    protected final Function<K, V> loader;
    protected final Function<K, KK> keyMapper;
    public static final Function IDENTITY = new Function() {
        @Override
        public Object apply(Object key) {
            return key;
        }
    };

    public LoadingCache(Function<K, KK> keyMapper, Function<K, V> loader) {
        this.keyMapper = keyMapper;
        this.loader = loader;
        this.map = new ConcurrentHashMap();
    }

    public static <K> Function<K, K> identity() {
        return IDENTITY;
    }

    public V get(K key) {
        KK cacheKey = this.keyMapper.apply(key);
        Object v = this.map.get(cacheKey);
        return v != null && !(v instanceof FutureTask) ? (V) v : this.createEntry(key, cacheKey, v);
    }

    protected V createEntry(final K key, KK cacheKey, Object v) {
        boolean creator = false;
        FutureTask task;
        Object result;
        if (v != null) {
            task = (FutureTask)v;
        } else {
            task = new FutureTask(new Callable<V>() {
                @Override
                public V call() throws Exception {
                    return LoadingCache.this.loader.apply(key);
                }
            });
            result = this.map.putIfAbsent(cacheKey, task);
            if (result == null) {
                creator = true;
                task.run();
            } else {
                if (!(result instanceof FutureTask)) {
                    return (V) result;
                }

                task = (FutureTask)result;
            }
        }

        try {
            result = task.get();
        } catch (InterruptedException var9) {
            throw new IllegalStateException("Interrupted while loading cache item", var9);
        } catch (ExecutionException var10) {
            Throwable cause = var10.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }

            throw new IllegalStateException("Unable to load cache item", cause);
        }

        if (creator) {
            this.map.put(cacheKey, result);
        }

        return (V) result;
    }
}
