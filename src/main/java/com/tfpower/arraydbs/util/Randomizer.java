package com.tfpower.arraydbs.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

public class Randomizer {

    private final static Logger logger = LoggerFactory.getLogger(Randomizer.class);

    public static final int MAX_SMALL_INT = 20;
    private static final long SEED = 1521233913850L;
//            System.currentTimeMillis();

    static {
        logger.info("Initialized randomizer with seed = {}", SEED);
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
