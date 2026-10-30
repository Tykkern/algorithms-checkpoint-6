package kt6;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка чтения входных данных и формата вывода для каждой задачи.
 */
class MainTest {

    private static String run(int task, String input) throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter out = new PrintWriter(output);
        FastReader in = new FastReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        Main.solve(task, in, out);
        out.flush();
        return output.toString().replace("\r\n", "\n");
    }

    @Test
    void task1() throws IOException {
        assertEquals("1 5 9\n", run(1, "5\n1 1 2\n2 3 -1\n3 -1 4\n4 -1 -1\n5 -1 -1\n"));
    }

    @Test
    void task2() throws IOException {
        assertEquals("1 2\n", run(2, "6 2\n1 1 1 2 2 3\n"));
        assertEquals("4 5\n", run(2, "5 2\n5 4 4 5 3\n"));
    }

    @Test
    void task3() throws IOException {
        assertEquals("1.0 2.0 2.0 1.5\n", run(3, "4\n1 3 2 -4\n"));
        assertEquals("-1.0 -0.5\n", run(3, "2\n-1 0\n"));
    }

    @Test
    void task4() throws IOException {
        assertEquals("42\n", run(4, "5\n-10 1 2\n9 -1 -1\n20 3 4\n15 -1 -1\n7 -1 -1\n"));
    }

    @Test
    void task5() throws IOException {
        assertEquals("9\n", run(5, "3 3 4\n1 7 11\n2 4 6\n"));
    }

    @Test
    void task6() throws IOException {
        assertEquals("1.0 -1.0 -1.0 3.0 5.0 6.0\n", run(6, "8 3\n1 3 -1 -3 5 3 6 7\n"));
        assertEquals("2.5 3.0 2.5\n", run(6, "4 2\n1 4 2 3\n"));
    }

    @Test
    void oneDecimalHandlesSignAndHalves() {
        assertEquals("0.0", Main.oneDecimal(0));
        assertEquals("-0.5", Main.oneDecimal(-0.5));
        assertEquals("-1.5", Main.oneDecimal(-1.5));
        assertEquals("7.0", Main.oneDecimal(7));
        assertEquals("999999999.5", Main.oneDecimal(999_999_999.5));
        assertEquals("-1000000000.0", Main.oneDecimal(-1e9));
    }

    @Test
    void windowsLineEndings() throws IOException {
        assertEquals("9\n", run(5, "3 3 4\r\n1 7 11\r\n2 4 6\r\n"));
    }

    @Test
    void truncatedInputThrows() {
        assertThrows(EOFException.class, () -> run(3, "3\n1 2\n"));
    }

    @Test
    void unknownTaskThrows() {
        assertThrows(IllegalArgumentException.class, () -> run(7, ""));
    }
}
