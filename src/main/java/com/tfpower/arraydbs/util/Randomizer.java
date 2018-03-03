package com.tfpower.arraydbs.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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

}
