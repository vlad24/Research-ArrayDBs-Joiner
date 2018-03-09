package com.tfpower.arraydbs.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;

@Component
@Scope("prototype")
public interface Cache<T> {

    T add(T newEntry);

    int getCurrentSize();

    int getCapacity();

    Set<T> getAllEntries();

    void clear(Predicate<T> predicate);

    T evict(Comparator<T> weighter);
}
