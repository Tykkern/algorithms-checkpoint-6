package kt6;

import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Задача 3. Медиана после каждого нового числа.
 */
public final class RunningMedian {

    private RunningMedian() {
    }

    /**
     * Две кучи: в lower меньшая половина чисел (наверху её максимум), в upper
     * большая (наверху минимум). lower содержит столько же чисел, сколько upper,
     * или на одно больше. Тогда медиана видна на вершинах: при нечётном
     * количестве это вершина lower, при чётном среднее двух вершин.
     *
     * Новое число сначала кладётся в lower, максимум lower переезжает в upper,
     * и если upper стала больше, её минимум возвращается в lower. Так обе
     * половины остаются упорядоченными относительно друг друга.
     *
     * O(n log n) по времени, O(n) по памяти.
     */
    public static double[] medians(int[] numbers) {
        PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
        PriorityQueue<Integer> upper = new PriorityQueue<>();
        double[] result = new double[numbers.length];

        for (int i = 0; i < numbers.length; i++) {
            lower.add(numbers[i]);
            upper.add(lower.poll());
            if (upper.size() > lower.size())
                lower.add(upper.poll());

            result[i] = lower.size() > upper.size()
                    ? lower.peek()
                    : ((long) lower.peek() + upper.peek()) / 2.0;
        }
        return result;
    }
}
