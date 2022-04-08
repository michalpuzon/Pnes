package org.ekipa.pnes.utils;

import java.util.List;
import java.util.Random;

public class MyRandom {

    public static int getRandom(int a, int b) {
        return (Math.abs(new Random().nextInt())) % (b - a + 1) + a;
    }

    /**
     * Zwracanie losowej wartości z listy.
     *
     * @param list Lista obiektów.
     * @param <E> Rodzaj obiektu.
     * @return null jeśli lista jest pusta, w przeciwnym przypadku zwraca losowy obiekt z listy.
     */
    public static <E> E getRandom(List<E> list) {
        if (list.isEmpty()) return null;
        return list.get(getRandom(0, list.size() - 1));
    }
}
