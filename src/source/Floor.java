package source;

import java.util.TreeMap;

/*
Map floor number
-3 ~ -1, 1 ~ 20
to
0 ~ 22
 */
public class Floor {
    public static final int[] toFloor = new int[23];
    public static final TreeMap<Integer, Integer> toIndex = new TreeMap<>();

    static {
        for (int i = 0; i < 23; i++) {
            if (i < 3) {
                toFloor[i] = i - 3;
                toIndex.put(i - 3, i);
            } else {
                toFloor[i] = i - 2;
                toIndex.put(i - 2, i);
            }
        }
    }
}
