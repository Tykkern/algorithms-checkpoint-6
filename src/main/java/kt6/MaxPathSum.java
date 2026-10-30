package kt6;

/**
 * Задача 4. Маршрут в бинарном дереве с максимальной стоимостью.
 */
public final class MaxPathSum {

    private MaxPathSum() {
    }

    /**
     * У любого маршрута есть самая верхняя вершина. Маршрут через неё состоит
     * из самой вершины и, возможно, лучших «спусков» в левое и правое
     * поддеревья. Спуск gain[v] - лучшая стоимость пути, который начинается
     * в v и идёт только вниз: value[v] плюс больший из спусков потомков,
     * если он положительный (отрицательный спуск выгоднее не брать).
     *
     * Потомки должны быть посчитаны раньше родителя, поэтому вершины
     * обрабатываются в порядке, обратном обходу в ширину. Рекурсии нет,
     * цепочка из 200 000 вершин не роняет стек.
     *
     * O(n) по времени и памяти.
     *
     * @param value значения вершин
     * @param left  индексы левых потомков, -1 если потомка нет
     * @param right индексы правых потомков, -1 если потомка нет
     */
    public static long maxSum(int[] value, int[] left, int[] right) {
        int n = value.length;
        int[] order = new int[n];
        int size = 0;
        order[size++] = 0;
        for (int head = 0; head < size; head++) {
            int node = order[head];
            if (left[node] >= 0)
                order[size++] = left[node];
            if (right[node] >= 0)
                order[size++] = right[node];
        }

        long[] gain = new long[n];
        long best = Long.MIN_VALUE;
        for (int i = n - 1; i >= 0; i--) {
            int node = order[i];
            long fromLeft = left[node] >= 0 ? Math.max(0, gain[left[node]]) : 0;
            long fromRight = right[node] >= 0 ? Math.max(0, gain[right[node]]) : 0;
            gain[node] = value[node] + Math.max(fromLeft, fromRight);
            best = Math.max(best, value[node] + fromLeft + fromRight);
        }
        return best;
    }
}
