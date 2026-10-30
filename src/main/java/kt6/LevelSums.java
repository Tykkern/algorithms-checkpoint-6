package kt6;

import java.util.Arrays;

/**
 * Задача 1. Суммы значений по уровням бинарного дерева.
 */
public final class LevelSums {

    private LevelSums() {
    }

    /**
     * Обход в ширину от корня 0 по уровням: очередь на массиве, в ней сначала
     * все вершины уровня 0, затем уровня 1 и так далее. Сумма уровня копится
     * в long, потому что 200 000 значений по 10^9 не помещаются в int.
     * Рекурсии нет, поэтому дерево-цепочка глубиной 200 000 не роняет стек.
     *
     * O(n) по времени и памяти.
     *
     * @param value значения вершин
     * @param left  индексы левых потомков, -1 если потомка нет
     * @param right индексы правых потомков, -1 если потомка нет
     */
    public static long[] sums(int[] value, int[] left, int[] right) {
        int n = value.length;
        int[] queue = new int[n];
        long[] sums = new long[n];
        int levels = 0;
        int head = 0;
        int tail = 0;
        queue[tail++] = 0;

        while (head < tail) {
            int levelEnd = tail;
            long sum = 0;
            while (head < levelEnd) {
                int node = queue[head++];
                sum += value[node];
                if (left[node] >= 0)
                    queue[tail++] = left[node];
                if (right[node] >= 0)
                    queue[tail++] = right[node];
            }
            sums[levels++] = sum;
        }
        return Arrays.copyOf(sums, levels);
    }
}
