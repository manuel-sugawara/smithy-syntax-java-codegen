package mx.sugus.codegen.util;

import java.io.File;

public class PathUtil {

    public static String from(String... args) {
        if (args.length == 0) {
            return "";
        }
        var buf = new StringBuilder(args[0]);
        for (var idx = 1; idx < args.length; ++idx) {
            buf.append(File.separatorChar);
            buf.append(args[idx]);
        }
        return buf.toString();
    }
}
