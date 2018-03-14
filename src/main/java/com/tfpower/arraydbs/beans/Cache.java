package com.tfpower.arraydbs.beans;

import com.tfpower.arraydbs.entity.Vertex;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;

@Component
@Scope("prototype")
public interface Cache<T> {

    static Comparator<CacheEntry<Vertex>> oldest() {
        return Comparator.comparingLong((ToLongFunction<CacheEntry<Vertex>>) CacheEntry::getTime).reversed();
    }

    CacheEntry<T> loadOrFail(T newEntry);
    Optional<T> loadOrEvict(T newEntry, Comparator<CacheEntry<T>> weigher);

    boolean contains(T value);

    int getCapacity();

    Set<T> getAllValues();

    int getCurrentSize();

    T evict(Comparator<CacheEntry<T>> weigher);

    Set<T> evictAll(Predicate<CacheEntry<T>> predicate);

    default Set<T> clear(){
        return evictAll(e -> true);
    }

    class CacheEntry<T> {

        long time;

        T value;

        public CacheEntry(long time, T value) {
            this.time = time;

            this.value = value;
        }

        public long getTime() {
            return time;
        }

        public T getValue() {
            return value;
        }

    }
}
