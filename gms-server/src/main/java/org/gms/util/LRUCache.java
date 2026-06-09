package org.gms.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU缓存实现
 * 基于LinkedHashMap实现最近最少使用淘汰策略
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    /** 缓存容量上限 */
    private final int capacity;

    /**
     * 默认构造，容量1024
     */
    public LRUCache() {
        this(1024);
    }

    /**
     * 指定容量的构造
     *
     * @param capacity 缓存容量上限
     */
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    /**
     * 判断是否删除最旧条目
     * 当数据量超过容量上限时返回true，自动删除最老的数据
     *
     * @param eldest 最旧的条目
     * @return true表示删除
     */
    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}