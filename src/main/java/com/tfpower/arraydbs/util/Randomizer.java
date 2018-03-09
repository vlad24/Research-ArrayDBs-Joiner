package com.tfpower.arraydbs.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class Randomizer {

    private static Random random = new Random();

    public static <T> T pickRandomFrom(Collection<T> collection){
        int targetElementPos = random.nextInt(collection.size());
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < targetElementPos; i++){
            iterator.next();
        }
        return iterator.next();
    }

    public static Integer randomIntBetween(int start, int end) {
        return random.nextInt(end - start + 1) + start;
    }
}
