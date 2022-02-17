package banking;

import java.util.Objects;

public final class ArgsHelper {

    public static final String DB_NAME_ARG = "-fileName";

    public static String getDbPath(String[] args) {
        Objects.requireNonNull(args);
        if (args.length < 2) {
            throw new IllegalStateException("At least 2 command line arguments expected!");
        }
        for (int i = 0; i < args.length; i++) {
            if (DB_NAME_ARG.equals(args[i])) {
                if (i == args.length - 1) {
                    throw new IllegalStateException("`Path to db` should be followed by %s param".formatted(DB_NAME_ARG));
                }
                return args[i + 1];
            }
        }
        throw new IllegalStateException("%s param is not present".formatted(DB_NAME_ARG));
    }
}
