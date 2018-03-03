package com.tfpower.arraydbs.beans;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Set;

@Component
public interface Cache<T> {
    T tryAdd(T currentVertex, Comparator<T> comparator);
    Set<T> getAllEntries();
}
