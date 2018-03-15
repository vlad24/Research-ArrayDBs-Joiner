package com.tfpower.arraydbs.beans.impl;

import com.tfpower.arraydbs.beans.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class CacheImpl<T> implements Cache<T> {

    private AtomicInteger timer;
    private int capacity;
    private Set<CacheEntry<T>> cachedItems;

    @Autowired
    public CacheImpl(@Value("${cache.capacity}") Integer capacity) {
        this.capacity = capacity;
        this.cachedItems = new HashSet<>(capacity);
        this.timer = new AtomicInteger(0);
    }

    @Override
    public void loadOrFail(T newEntry) {
        CacheEntry<T> newCacheEntry = new CacheEntry<>(timer.getAndIncrement(), newEntry);
        if (cachedItems.size() < capacity) {
            cachedItems.add(newCacheEntry);
        } else {
            throw new IllegalStateException(this + " has no more space to store " + newEntry);
        }
    }


    @Override
    public Optional<T> loadOrEvict(T newEntry, Comparator<CacheEntry<T>> weigher) {
        CacheEntry<T> newCacheEntry = new CacheEntry<>(timer.getAndIncrement(), newEntry);
        if (cachedItems.size() < capacity) {
            cachedItems.add(newCacheEntry);
            return Optional.empty();
        } else {
            T evicted = evict(weigher);
            cachedItems.add(newCacheEntry);
            return Optional.ofNullable(evicted);
        }
    }


    @Override
    public int getCurrentSize() {
        return cachedItems.size();
    }

    @Override
    public boolean contains(T searchedValue) {
        return cachedItems.stream().anyMatch(e -> e.getValue().equals(searchedValue));
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public Set<T> getAllValues() {
        return cachedItems.stream().map(CacheEntry::getValue).collect(Collectors.toSet());
    }

    @Override
    public Set<T> evictAll(Predicate<CacheEntry<T>> predicate) {
        Set<CacheEntry<T>> toRemove = cachedItems.stream().filter(predicate).collect(Collectors.toSet());
        cachedItems.removeAll(toRemove);
        return toRemove.stream().map(CacheEntry::getValue).collect(Collectors.toSet());
    }

    @Override
    public T evict(Comparator<CacheEntry<T>> weigher) {
        Optional<CacheEntry<T>> candidate = cachedItems.stream().max(weigher);
        candidate.ifPresent(t -> cachedItems.remove(t));
        return candidate.map(CacheEntry::getValue).orElse(null);
    }

    @Override
    public String toString() {
        List<T> cacheValues = new ArrayList<>(getAllValues());
        cacheValues.sort(Comparator.comparing(Object::toString));
        return "Cache<" + getCurrentSize() + "/" + capacity + "> " + cacheValues;
    }
}
