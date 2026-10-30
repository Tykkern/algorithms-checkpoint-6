package kt6;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * Задача 6. Медиана каждого окна длины k.
 */
public final class WindowMedian {

    private WindowMedian() {
    }

    /**
     * Идея как в задаче 3, но из окна нужно ещё и удалять уходящий элемент,
     * а куча умеет быстро удалять только вершину. Поэтому половины хранятся
     * в двух TreeSet: в lower меньшие k/2 с округлением вверх, в upper остальные.
     * В множествах лежат индексы, упорядоченные по (значение, индекс), так что
     * одинаковые значения не сливаются и любой индекс удаляется за O(log k).
     *
     * Шаг окна: добавить новый индекс в нужную половину, удалить ушедший,
     * выровнять размеры перекладыванием крайних элементов. Медиана берётся
     * с краёв половин, как в задаче 3.
     *
     * O(n log k) по времени, O(k) по памяти.
     */
    public static double[] medians(int[] numbers, int k) {
        Comparator<Integer> order = (x, y) -> numbers[x] != numbers[y]
                ? Integer.compare(numbers[x], numbers[y])
                : Integer.compare(x, y);
        TreeSet<Integer> lower = new TreeSet<>(order);
        TreeSet<Integer> upper = new TreeSet<>(order);
        double[] result = new double[numbers.length - k + 1];

        for (int i = 0; i < numbers.length; i++) {
            if (!lower.isEmpty() && order.compare(i, lower.last()) > 0)
                upper.add(i);
            else
                lower.add(i);

            if (i >= k && !lower.remove(i - k))
                upper.remove(i - k);

            while (lower.size() > upper.size() + 1)
                upper.add(lower.pollLast());
            while (upper.size() > lower.size())
                lower.add(upper.pollFirst());

            if (i >= k - 1)
                result[i - k + 1] = k % 2 == 1
                        ? numbers[lower.last()]
                        : ((long) numbers[lower.last()] + numbers[upper.first()]) / 2.0;
        }
        return result;
    }
}
