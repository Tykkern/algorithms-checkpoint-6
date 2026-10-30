package kt6;

import java.util.PriorityQueue;

/**
 * Задача 5. k-я наименьшая сумма A[i] + B[j].
 */
public final class KthPairSum {

    private KthPairSum() {
    }

    /**
     * Суммы с фиксированным i образуют возрастающую строку
     * A[i] + B[0] <= A[i] + B[1] <= ..., то есть это слияние n отсортированных
     * списков. В куче лежит по одному кандидату из каждой строки, наверху
     * наименьшая сумма. Вынули пару (i, j) - на её место встаёт (i, j + 1).
     * k-я вынутая пара и есть ответ.
     *
     * Строк с номером k и больше не нужно: в них каждая сумма не меньше
     * k сумм A[0..k-1] + B[0]. Поэтому в куче не больше min(n, k) пар.
     *
     * O(k log min(n, k)) по времени, O(min(n, k)) по памяти.
     */
    public static long kth(int[] a, int[] b, int k) {
        // Элемент кучи: {i, j}.
        PriorityQueue<int[]> heap = new PriorityQueue<>(
                (x, y) -> Long.compare((long) a[x[0]] + b[x[1]], (long) a[y[0]] + b[y[1]]));
        for (int i = 0; i < Math.min(a.length, k); i++)
            heap.add(new int[] {i, 0});

        for (int taken = 1; taken < k; taken++) {
            int[] pair = heap.poll();
            if (pair[1] + 1 < b.length)
                heap.add(new int[] {pair[0], pair[1] + 1});
        }
        int[] pair = heap.peek();
        return (long) a[pair[0]] + b[pair[1]];
    }
}
