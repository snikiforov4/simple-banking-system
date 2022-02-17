package banking;

import java.io.PrintStream;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleHelper {
    private static final Scanner IN = new Scanner(System.in);
    private static final PrintStream OUT = System.out;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    public static void println(String text) {
        OUT.println(text);
    }

    public static void println() {
        OUT.println();
    }

    public static String read() {
        return IN.next();
    }

    public static OptionalInt tryReadInt() {
        String input = read();
        if ((NUMBER_PATTERN.matcher(input).matches())) {
            return OptionalInt.of(Integer.parseInt(input));
        }
        return OptionalInt.empty();
    }
}
