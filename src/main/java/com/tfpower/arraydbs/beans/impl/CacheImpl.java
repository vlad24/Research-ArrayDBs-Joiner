package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Component
public class CacheImpl<T> implements Cache<T> {

    private int capacity;
    private Set<T> cachedItems;

    @Autowired
    public CacheImpl(@Value("${cache_capacity}") Integer capacity) {
        this.capacity = capacity;
        this.cachedItems = new HashSet<>(capacity);
    }

    @Override
    public T tryAdd(T newEntry, Comparator<T> weighter) {
        if (cachedItems.size() < capacity) {
            cachedItems.add(newEntry);
            return null;
        } else {
            T evictedEntry = cachedItems.stream().max(weighter).get();
            cachedItems.remove(evictedEntry);
            cachedItems.add(newEntry);
            return evictedEntry;
        }
    }

    @Override
    public Set<T> getAllEntries() {
        return cachedItems;
    }

    @Override
    public String toString() {
        return "Cache< " + capacity + ">: " + cachedItems + " | " + cachedItems.size();
    }
}
