package kt6;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Задача 2. k самых частых чисел.
 */
public final class TopFrequent {

    private TopFrequent() {
    }

    /**
     * Частоты считаются в HashMap. Затем числа проходят через кучу размера k,
     * на вершине которой худший из отобранных: с меньшей частотой, а при равной
     * частоте с большим значением. Если очередное число лучше вершины, вершина
     * вытесняется. В конце куча разбирается от худшего к лучшему и заполняет
     * ответ с конца.
     *
     * O(n + d log k) по времени, где d - число различных значений, O(d) по памяти.
     *
     * @return k чисел по убыванию частоты, при равной частоте по возрастанию
     */
    public static int[] top(int[] numbers, int k) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int number : numbers)
            counts.merge(number, 1, Integer::sum);

        // Элемент кучи: {значение, частота}. Наверху худший.
        PriorityQueue<int[]> heap = new PriorityQueue<>(
                (x, y) -> x[1] != y[1] ? Integer.compare(x[1], y[1]) : Integer.compare(y[0], x[0]));
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            heap.add(new int[] {entry.getKey(), entry.getValue()});
            if (heap.size() > k)
                heap.poll();
        }

        int[] answer = new int[k];
        for (int i = k - 1; i >= 0; i--)
            answer[i] = heap.poll()[0];
        return answer;
    }
}
