package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.Cache;
import com.tfpower.arraydbs.util.Randomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

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
    public T add(T newEntry) {
        if (cachedItems.size() < capacity) {
            cachedItems.add(newEntry);
            return null;
        } else {
            throw new IllegalStateException(this + " has no more space to store " + newEntry);
        }
    }

    @Override
    public int getCurrentSize() {
        return cachedItems.size();
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public Set<T> getAllEntries() {
        return cachedItems;
    }

    @Override
    public void clear(Predicate<T> predicate) {
        cachedItems.removeIf(predicate);
    }

    @Override
    public T evict(Comparator<T> weighter) {
        Optional<T> candidate = cachedItems.stream().max(weighter);
        candidate.ifPresent(t -> cachedItems.remove(t));
        return candidate.orElse(null);
    }

    @Override
    public String toString() {
        return "Cache<" + getCurrentSize() + "/" + capacity + "> " + cachedItems ;
    }
}
