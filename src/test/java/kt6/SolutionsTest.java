package kt6;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SolutionsTest {

    private static final Random RANDOM = new Random(1);

    private static int randomValue(int bound) {
        return RANDOM.nextInt(2 * bound + 1) - bound;
    }

    /**
     * Случайное бинарное дерево из n вершин в виде {value, left, right}.
     * Корень 0, номера остальных вершин перемешаны, чтобы потомок не всегда
     * имел номер больше родителя.
     */
    private static int[][] randomTree(int n, int bound) {
        List<Integer> labels = new ArrayList<>();
        for (int i = 1; i < n; i++)
            labels.add(i);
        Collections.shuffle(labels, RANDOM);
        labels.add(0, 0);

        int[] value = new int[n];
        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, -1);
        Arrays.fill(right, -1);
        for (int created = 1; created < n; created++) {
            int child = labels.get(created);
            while (true) {
                int parent = labels.get(RANDOM.nextInt(created));
                int[] side = RANDOM.nextBoolean() ? left : right;
                if (side[parent] < 0) {
                    side[parent] = child;
                    break;
                }
            }
        }
        for (int i = 0; i < n; i++)
            value[i] = randomValue(bound);
        return new int[][] {value, left, right};
    }

    // Цепочка из левых потомков: 0 -> 1 -> ... -> n-1, все значения равны value.
    private static int[][] chain(int n, int value) {
        int[][] tree = new int[3][n];
        Arrays.fill(tree[0], value);
        for (int i = 0; i < n; i++) {
            tree[1][i] = i + 1 < n ? i + 1 : -1;
            tree[2][i] = -1;
        }
        return tree;
    }

    // Полное дерево в нумерации кучи: потомки вершины i это 2i+1 и 2i+2.
    private static int[][] complete(int n, int value) {
        int[][] tree = new int[3][n];
        Arrays.fill(tree[0], value);
        for (int i = 0; i < n; i++) {
            tree[1][i] = 2 * i + 1 < n ? 2 * i + 1 : -1;
            tree[2][i] = 2 * i + 2 < n ? 2 * i + 2 : -1;
        }
        return tree;
    }

    private static double median(int[] sorted) {
        int n = sorted.length;
        return n % 2 == 1 ? sorted[n / 2] : ((long) sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
    }

    @Nested
    class LevelSumsTest {

        private void collect(int[][] tree, int node, int depth, List<Long> sums) {
            if (node < 0)
                return;
            if (sums.size() == depth)
                sums.add(0L);
            sums.set(depth, sums.get(depth) + tree[0][node]);
            collect(tree, tree[1][node], depth + 1, sums);
            collect(tree, tree[2][node], depth + 1, sums);
        }

        private long[] bruteForce(int[][] tree) {
            List<Long> sums = new ArrayList<>();
            collect(tree, 0, 0, sums);
            return sums.stream().mapToLong(Long::longValue).toArray();
        }

        @Test
        void singleNode() {
            assertArrayEquals(new long[] {-7}, LevelSums.sums(new int[] {-7}, new int[] {-1}, new int[] {-1}));
        }

        @Test
        void smallTree() {
            // 1 -> (2, 3), 2 -> 4 слева, 3 -> 5 справа
            assertArrayEquals(new long[] {1, 5, 9}, LevelSums.sums(
                    new int[] {1, 2, 3, 4, 5},
                    new int[] {1, 3, -1, -1, -1},
                    new int[] {2, -1, 4, -1, -1}));
        }

        @Test
        void randomTreesMatchBruteForce() {
            for (int test = 0; test < 300; test++) {
                int[][] tree = randomTree(1 + RANDOM.nextInt(40), 1_000_000_000);
                assertArrayEquals(bruteForce(tree), LevelSums.sums(tree[0], tree[1], tree[2]));
            }
        }

        @Test
        @Timeout(5)
        void deepChainWithoutStackOverflow() {
            int n = 200_000;
            int[][] tree = chain(n, 1_000_000_000);
            long[] sums = LevelSums.sums(tree[0], tree[1], tree[2]);
            assertEquals(n, sums.length);
            assertEquals(1_000_000_000L, sums[n - 1]);
        }

        @Test
        @Timeout(5)
        void wideLevelsNeedLong() {
            int n = 200_000;
            int[][] tree = complete(n, 1_000_000_000);
            long[] sums = LevelSums.sums(tree[0], tree[1], tree[2]);
            // Уровни 0..16 полные (2^17 - 1 = 131 071 вершина), на уровне 17 остальные.
            assertEquals(18, sums.length);
            assertEquals(65_536L * 1_000_000_000L, sums[16]);
            assertEquals((n - 131_071L) * 1_000_000_000L, sums[17]);
        }
    }

    @Nested
    class TopFrequentTest {

        private int[] bruteForce(int[] numbers, int k) {
            Map<Integer, Integer> counts = new HashMap<>();
            for (int number : numbers)
                counts.merge(number, 1, Integer::sum);
            List<Integer> distinct = new ArrayList<>(counts.keySet());
            distinct.sort((x, y) -> !counts.get(x).equals(counts.get(y))
                    ? Integer.compare(counts.get(y), counts.get(x))
                    : Integer.compare(x, y));
            return distinct.subList(0, k).stream().mapToInt(Integer::intValue).toArray();
        }

        @Test
        void basicCase() {
            assertArrayEquals(new int[] {1, 2}, TopFrequent.top(new int[] {1, 1, 1, 2, 2, 3}, 2));
        }

        @Test
        void equalFrequencySmallerFirst() {
            assertArrayEquals(new int[] {4, 5}, TopFrequent.top(new int[] {5, 4, 4, 5, 3}, 2));
            assertArrayEquals(new int[] {-3, 0, 7}, TopFrequent.top(new int[] {7, 0, -3}, 3));
        }

        @Test
        void randomArraysMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int[] numbers = RANDOM.ints(1 + RANDOM.nextInt(40), -5, 6).toArray();
                int distinct = (int) Arrays.stream(numbers).distinct().count();
                int k = 1 + RANDOM.nextInt(distinct);
                assertArrayEquals(bruteForce(numbers, k), TopFrequent.top(numbers, k));
            }
        }

        @Test
        @Timeout(5)
        void allDistinctMaxSize() {
            int n = 200_000;
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < n; i++)
                values.add(i * 5_000 - 500_000_000);
            Collections.shuffle(values, RANDOM);
            int[] numbers = values.stream().mapToInt(Integer::intValue).toArray();

            int[] answer = TopFrequent.top(numbers, n);
            for (int i = 0; i < n; i++)
                assertEquals(i * 5_000 - 500_000_000, answer[i]);
        }
    }

    @Nested
    class RunningMedianTest {

        @Test
        void basicCase() {
            assertArrayEquals(new double[] {1, 2, 2, 1.5}, RunningMedian.medians(new int[] {1, 3, 2, -4}));
        }

        @Test
        void extremeValuesDoNotOverflow() {
            assertArrayEquals(new double[] {1e9, 1e9}, RunningMedian.medians(new int[] {1_000_000_000, 1_000_000_000}));
            assertArrayEquals(new double[] {-1e9, -1e9 + 0.5},
                    RunningMedian.medians(new int[] {-1_000_000_000, -999_999_999}));
        }

        @Test
        void randomSequencesMatchSorting() {
            for (int test = 0; test < 300; test++) {
                int[] numbers = RANDOM.ints(1 + RANDOM.nextInt(40), -10, 11).toArray();
                double[] medians = RunningMedian.medians(numbers);
                for (int i = 0; i < numbers.length; i++) {
                    int[] prefix = Arrays.copyOf(numbers, i + 1);
                    Arrays.sort(prefix);
                    assertEquals(median(prefix), medians[i]);
                }
            }
        }

        @Test
        @Timeout(5)
        void increasingMaxSize() {
            int n = 200_000;
            int[] numbers = new int[n];
            for (int i = 0; i < n; i++)
                numbers[i] = i + 1;
            double[] medians = RunningMedian.medians(numbers);
            for (int i = 0; i < n; i++)
                assertEquals((i + 2) / 2.0, medians[i]);
        }
    }

    @Nested
    class MaxPathSumTest {

        // Перебор всех пар концов маршрута через общего предка.
        private long bruteForce(int[][] tree) {
            int n = tree[0].length;
            int[] parent = new int[n];
            Arrays.fill(parent, -1);
            for (int v = 0; v < n; v++) {
                if (tree[1][v] >= 0)
                    parent[tree[1][v]] = v;
                if (tree[2][v] >= 0)
                    parent[tree[2][v]] = v;
            }

            long best = Long.MIN_VALUE;
            for (int u = 0; u < n; u++) {
                Set<Integer> ancestors = new HashSet<>();
                for (int x = u; x >= 0; x = parent[x])
                    ancestors.add(x);
                for (int v = 0; v < n; v++) {
                    long sum = 0;
                    int meet = v;
                    while (!ancestors.contains(meet)) {
                        sum += tree[0][meet];
                        meet = parent[meet];
                    }
                    for (int x = u; x != meet; x = parent[x])
                        sum += tree[0][x];
                    best = Math.max(best, sum + tree[0][meet]);
                }
            }
            return best;
        }

        @Test
        void routeAvoidsRoot() {
            // -10 -> (9, 20), 20 -> (15, 7): лучший маршрут 15 - 20 - 7
            assertEquals(42, MaxPathSum.maxSum(
                    new int[] {-10, 9, 20, 15, 7},
                    new int[] {1, -1, 3, -1, -1},
                    new int[] {2, -1, 4, -1, -1}));
        }

        @Test
        void allNegativeTakesSingleNode() {
            assertEquals(-2, MaxPathSum.maxSum(new int[] {-3, -2, -5}, new int[] {1, -1, -1}, new int[] {2, -1, -1}));
            assertEquals(-1_000_000_000, MaxPathSum.maxSum(new int[] {-1_000_000_000}, new int[] {-1}, new int[] {-1}));
        }

        @Test
        void randomTreesMatchBruteForce() {
            for (int test = 0; test < 300; test++) {
                int[][] tree = randomTree(1 + RANDOM.nextInt(25), 20);
                assertEquals(bruteForce(tree), MaxPathSum.maxSum(tree[0], tree[1], tree[2]));
            }
        }

        @Test
        @Timeout(5)
        void deepChainNeedsLong() {
            int n = 200_000;
            int[][] tree = chain(n, 1_000_000_000);
            assertEquals(n * 1_000_000_000L, MaxPathSum.maxSum(tree[0], tree[1], tree[2]));
        }

        @Test
        @Timeout(5)
        void completeTreeOfNegatives() {
            int[][] tree = complete(200_000, -1_000_000_000);
            assertEquals(-1_000_000_000L, MaxPathSum.maxSum(tree[0], tree[1], tree[2]));
        }
    }

    @Nested
    class KthPairSumTest {

        private long bruteForce(int[] a, int[] b, int k) {
            long[] sums = new long[a.length * b.length];
            int count = 0;
            for (int x : a)
                for (int y : b)
                    sums[count++] = (long) x + y;
            Arrays.sort(sums);
            return sums[k - 1];
        }

        private int[] sortedRandom(int length, int bound) {
            int[] values = new int[length];
            for (int i = 0; i < length; i++)
                values[i] = randomValue(bound);
            Arrays.sort(values);
            return values;
        }

        @Test
        void basicCase() {
            int[] a = {1, 7, 11};
            int[] b = {2, 4, 6};
            long[] expected = {3, 5, 7, 9, 11, 13, 13, 15, 17};
            for (int k = 1; k <= 9; k++)
                assertEquals(expected[k - 1], KthPairSum.kth(a, b, k));
        }

        @Test
        void extremeValues() {
            int[] max = {1_000_000_000};
            int[] min = {-1_000_000_000};
            assertEquals(2_000_000_000L, KthPairSum.kth(max, max, 1));
            assertEquals(-2_000_000_000L, KthPairSum.kth(min, min, 1));
        }

        @Test
        void randomArraysMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int[] a = sortedRandom(1 + RANDOM.nextInt(15), 10);
                int[] b = sortedRandom(1 + RANDOM.nextInt(15), 10);
                int k = 1 + RANDOM.nextInt(a.length * b.length);
                assertEquals(bruteForce(a, b, k), KthPairSum.kth(a, b, k));
            }
        }

        @Test
        @Timeout(5)
        void maxSize() {
            int n = 200_000;
            int[] a = new int[n];
            for (int i = 0; i < n; i++)
                a[i] = i;
            // Пар с суммой не больше s ровно (s + 1)(s + 2) / 2, пока s < n.
            int k = 200_000;
            long s = 0;
            while ((s + 1) * (s + 2) / 2 < k)
                s++;
            assertEquals(s, KthPairSum.kth(a, a, k));
        }
    }

    @Nested
    class WindowMedianTest {

        private double[] bruteForce(int[] numbers, int k) {
            double[] result = new double[numbers.length - k + 1];
            for (int start = 0; start < result.length; start++) {
                int[] window = Arrays.copyOfRange(numbers, start, start + k);
                Arrays.sort(window);
                result[start] = median(window);
            }
            return result;
        }

        @Test
        void oddWindow() {
            assertArrayEquals(new double[] {1, -1, -1, 3, 5, 6},
                    WindowMedian.medians(new int[] {1, 3, -1, -3, 5, 3, 6, 7}, 3));
        }

        @Test
        void evenWindowAndWholeArray() {
            assertArrayEquals(new double[] {2.5, 3, 2.5}, WindowMedian.medians(new int[] {1, 4, 2, 3}, 2));
            assertArrayEquals(new double[] {2.5}, WindowMedian.medians(new int[] {1, 4, 2, 3}, 4));
        }

        @Test
        void equalValuesLeaveCorrectly() {
            assertArrayEquals(new double[] {5, 5, 3, 1},
                    WindowMedian.medians(new int[] {5, 5, 5, 1, 1}, 2));
        }

        @Test
        void randomArraysMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int[] numbers = RANDOM.ints(1 + RANDOM.nextInt(40), -5, 6).toArray();
                int k = 1 + RANDOM.nextInt(numbers.length);
                assertArrayEquals(bruteForce(numbers, k), WindowMedian.medians(numbers, k));
            }
        }

        @Test
        @Timeout(5)
        void increasingMaxSize() {
            int n = 200_000;
            int k = 100_000;
            int[] numbers = new int[n];
            for (int i = 0; i < n; i++)
                numbers[i] = i;
            double[] medians = WindowMedian.medians(numbers, k);
            assertEquals(n - k + 1, medians.length);
            for (int start = 0; start < medians.length; start++)
                assertEquals(start + (k - 1) / 2.0, medians[start]);
        }

        @Test
        @Timeout(5)
        void randomMaxSizeWithSingleElementWindow() {
            int[] numbers = RANDOM.ints(200_000, -1_000_000_000, 1_000_000_001).toArray();
            double[] medians = WindowMedian.medians(numbers, 1);
            for (int i = 0; i < numbers.length; i++)
                assertEquals(numbers[i], medians[i]);
        }
    }
}
