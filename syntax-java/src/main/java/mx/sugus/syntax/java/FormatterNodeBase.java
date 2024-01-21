package mx.sugus.syntax.java;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import mx.sugus.javapoet.TypeName;

public class FormatterNodeBase {

    static void checkArgument(boolean condition, String format, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

    public static StatementFormatter formatStatement(String format, Object... args) {
        var parts = parseFormat(format, args);
        return StatementFormatter.builder()
                .parts(parts)
                .build();
    }

    public static ExpressionFormatter formatExpression(String format, Object... args) {
        var parts = parseFormat(format, args);
        return ExpressionFormatter.builder()
                .parts(parts)
                .build();
    }

    static List<FormatterNode> parseFormat(String format, Object[] args) {
        List<String> formatParts = new ArrayList<>();
        List<Object> arguments = new ArrayList<>();
        boolean hasRelative = false;
        boolean hasIndexed = false;

        int relativeParameterCount = 0;
        int[] indexedParameterCount = new int[args.length];

        for (int p = 0; p < format.length(); ) {
            if (format.charAt(p) != '$') {
                int nextP = format.indexOf('$', p + 1);
                if (nextP == -1) {
                    nextP = format.length();
                }
                formatParts.add(format.substring(p, nextP));
                p = nextP;
                continue;
            }
            p++; // '$'.
            // Consume zero or more digits, leaving 'c' as the first non-digit char after the '$'.
            int indexStart = p;
            char c;
            do {
                checkArgument(p < format.length(), "dangling format characters in '%s'", format);
                c = format.charAt(p++);
            } while (c >= '0' && c <= '9');
            int indexEnd = p - 1;

            // If 'c' doesn't take an argument, we're done.
            if (isNoArgPlaceholder(c)) {
                checkArgument(indexStart == indexEnd, "$$, $>, $<, $[, $], $W, and $Z may not have an index");
                formatParts.add("$" + c);
                continue;
            }

            // Find either the indexed argument, or the relative argument. (0-based).
            int index;
            if (indexStart < indexEnd) {
                index = Integer.parseInt(format.substring(indexStart, indexEnd)) - 1;
                hasIndexed = true;
                if (args.length > 0) {
                    indexedParameterCount[index % args.length]++; // modulo is needed, checked below anyway
                }
            } else {
                index = relativeParameterCount;
                hasRelative = true;
                relativeParameterCount++;
            }

            checkArgument(index >= 0 && index < args.length, "index %d for '%s' not in range (received %s arguments)", index + 1, format.substring(indexStart - 1, indexEnd + 1), args.length);
            checkArgument(!hasIndexed || !hasRelative, "cannot mix indexed and positional parameters");

            addArgument(arguments, format, c, args[index]);
            formatParts.add("$" + c);
        }

        if (hasRelative) {
            checkArgument(relativeParameterCount >= args.length, "unused arguments: expected %s, received %s", relativeParameterCount, args.length);
        }
        if (hasIndexed) {
            List<String> unused = new ArrayList<>();
            for (int i = 0; i < args.length; i++) {
                if (indexedParameterCount[i] == 0) {
                    unused.add("$" + (i + 1));
                }
            }
            String s = unused.size() == 1 ? "" : "s";
            checkArgument(unused.isEmpty(), "unused argument%s: %s", s, String.join(", ", unused));
        }
        List<FormatterNode> parts = new ArrayList<>(formatParts.size());
        var argumentIndex = 0;
        for (var part : formatParts) {
            if (!part.startsWith("$")) {
                parts.add(FormatterLiteral.builder().value(part).build());
            }
            switch (part) {
                case "$$" -> parts.add(FormatterLiteral.builder().value("$").build());
                case "$N" -> parts.add(FormatterName.builder().value(arguments.get(argumentIndex++)).build());
                case "$S" ->
                        parts.add(FormatterString.builder().value((String) arguments.get(argumentIndex++)).build());
                case "$T" ->
                        parts.add(FormatterTypeName.builder().value((TypeName) arguments.get(argumentIndex++)).build());
            }
        }
        return parts;
    }

    static boolean isNoArgPlaceholder(char c) {
        return c == '$' || c == '>' || c == '<' || c == '[' || c == ']' || c == 'W' || c == 'Z';
    }

    private static void addArgument(List<Object> arguments, String format, char c, Object arg) {
        switch (c) {
            case 'N' -> arguments.add(argToName(arg));
            case 'L' -> arguments.add(argToLiteral(arg));
            case 'S' -> arguments.add(argToString(arg));
            case 'T' -> arguments.add(argToType(arg));
            default -> throw new IllegalArgumentException(String.format("invalid format string: '%s'", format));
        }
    }

    private static String argToName(Object o) {
        return String.valueOf(o);
    }

    private static Object argToLiteral(Object o) {
        return o;
    }

    private static String argToString(Object o) {
        return o != null ? String.valueOf(o) : null;
    }

    private static TypeName argToType(Object o) {
        if (o instanceof TypeName) {
            return (TypeName) o;
        }
        if (o instanceof TypeMirror) {
            return TypeName.get((TypeMirror) o);
        }
        if (o instanceof Element) {
            return TypeName.get(((Element) o).asType());
        }
        if (o instanceof Type) {
            return TypeName.get((Type) o);
        }
        throw new IllegalArgumentException("expected type but was " + o);
    }
}
