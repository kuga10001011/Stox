package org.kuga.stox;

import java.util.Random;

public class Engine {

    public static boolean toBuy(Stock stock, Long time) {
        Random random = new Random();
        return random.nextBoolean();
    }

    public static boolean toSell(Stock stock, Long time) {
        Random random = new Random();
        return random.nextBoolean();
    }

}
