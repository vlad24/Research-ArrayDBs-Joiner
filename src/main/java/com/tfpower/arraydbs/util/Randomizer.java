package com.tfpower.arraydbs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class Randomizer {

    private final static Logger logger = LoggerFactory.getLogger(Randomizer.class);

    private static final int MAX_SMALL_INT = 20;
    private static final long SEED =
            System.currentTimeMillis()
//            1521280451871L
            ;


    static {
        logger.info("Randomizer initialized with seed = {}", SEED);
    }

    private static Random random = new Random(SEED);


    public static <T> T pickRandomFrom(Collection<T> collection) {
        int targetElementPos = random.nextInt(collection.size());
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < targetElementPos; i++) {
            iterator.next();
        }
        return iterator.next();
    }


    public static Integer randomIntBetween(int start, int end) {
        return random.nextInt(end - start + 1) + start;
    }


    public static Integer randomPositiveSmallInt() {
        return randomIntBetween(1, MAX_SMALL_INT);
    }

    public static boolean randomBoolean() {
        return random.nextBoolean();
    }
}
