package kt6;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Запуск решения по номеру задачи: входные данные читаются из stdin
 * в формате из задания, ответ печатается в stdout.
 *
 * Пример: gradlew run -q --args="2" < input.txt
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Укажите номер задачи от 1 до 6, входные данные подаются в stdin.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        solve(Integer.parseInt(args[0]), new FastReader(System.in), out);
        out.flush();
    }

    static void solve(int task, FastReader in, PrintWriter out) throws IOException {
        switch (task) {
            case 1 -> {
                int[][] tree = readTree(in);
                StringBuilder line = new StringBuilder();
                for (long sum : LevelSums.sums(tree[0], tree[1], tree[2]))
                    line.append(line.length() > 0 ? " " : "").append(sum);
                out.println(line);
            }
            case 2 -> {
                int n = in.nextInt();
                int k = in.nextInt();
                StringBuilder line = new StringBuilder();
                for (int number : TopFrequent.top(in.nextInts(n), k))
                    line.append(line.length() > 0 ? " " : "").append(number);
                out.println(line);
            }
            case 3 -> out.println(join(RunningMedian.medians(in.nextInts(in.nextInt()))));
            case 4 -> {
                int[][] tree = readTree(in);
                out.println(MaxPathSum.maxSum(tree[0], tree[1], tree[2]));
            }
            case 5 -> {
                int n = in.nextInt();
                int m = in.nextInt();
                int k = in.nextInt();
                int[] a = in.nextInts(n);
                out.println(KthPairSum.kth(a, in.nextInts(m), k));
            }
            case 6 -> {
                int n = in.nextInt();
                int k = in.nextInt();
                out.println(join(WindowMedian.medians(in.nextInts(n), k)));
            }
            default -> throw new IllegalArgumentException("Нет задачи с номером " + task + ", есть 1-6.");
        }
    }

    // Формат: n, затем n строк value left right. Возвращает {value, left, right}.
    private static int[][] readTree(FastReader in) throws IOException {
        int n = in.nextInt();
        int[][] tree = new int[3][n];
        for (int i = 0; i < n; i++)
            for (int part = 0; part < 3; part++)
                tree[part][i] = in.nextInt();
        return tree;
    }

    private static String join(double[] medians) {
        StringBuilder line = new StringBuilder();
        for (double median : medians)
            line.append(line.length() > 0 ? " " : "").append(oneDecimal(median));
        return line.toString();
    }

    /**
     * Медиана целых чисел - это целое или половина, поэтому строка собирается
     * из удвоенного значения. String.format здесь не годится: в русской локали
     * он ставит запятую вместо точки, и на 200 000 чисел он медленный.
     */
    static String oneDecimal(double median) {
        long twice = Math.round(median * 2);
        long abs = Math.abs(twice);
        return (twice < 0 ? "-" : "") + abs / 2 + (abs % 2 == 0 ? ".0" : ".5");
    }
}
