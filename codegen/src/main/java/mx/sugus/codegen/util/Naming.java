package mx.sugus.codegen.util;

import static java.util.stream.Collectors.joining;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import software.amazon.smithy.utils.StringUtils;

public final class Naming {

    /* Parts of this code where copied from the AWS Java SDK.
     */

    private static final boolean DEBUG = false;


    // Copied from
    // https://github.com/aws/aws-sdk-java-v2/blob/5dd15f74beb6b5e50a22de26355047b80bf170c3/utils/src/main/java/software/amazon/awssdk/utils/internal/CodegenNamingUtils.java#L36
    public static String[] splitOnWordBoundaries(String toSplit) {
        var result = toSplit;

        // All non-alphanumeric characters are spaces
        result = result.replaceAll("[^A-Za-z0-9]+", " "); // acm-success -> "acm success"

        // If a number has a standalone v in front of it, separate it out (version).
        result = result.replaceAll("([^a-z]{2,})v([0-9]+)", "$1 v$2 ") // TESTv4 -> "TEST v4 "
                       .replaceAll("([^A-Z]{2,})V([0-9]+)", "$1 V$2 "); // TestV4 -> "Test V4 "

        // Add a space between camelCased words
        result = String.join(" ", result.split("(?<=[a-z])(?=[A-Z]([a-zA-Z]|[0-9]))")); // AcmSuccess -> // "Acm Success"

        // Add a space after acronyms
        result = result.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2"); // ACMSuccess -> "ACM Success"

        // Add space after a number in the middle of a word
        result = result.replaceAll("([0-9])([a-zA-Z])", "$1 $2"); // s3ec2 -> "s3 ec2"

        // Remove extra spaces - multiple consecutive ones or those and the beginning/end of words
        result = result.replaceAll(" +", " ") // "Foo  Bar" -> "Foo Bar"
                       .trim(); // " Foo " -> Foo

        return result.split(" ");
    }

    /**
     * Returns the argument formatted as CamelCase.
     */
    public static String camelCase(String word) {
        return Stream.of(splitOnWordBoundaries(word)).map(StringUtils::lowerCase).map(StringUtils::capitalize).collect(joining());
    }

    /**
     * Returns the argument formatted as pascalCase.
     */
    public static String pascalCase(String word) {
        var parts = splitOnWordBoundaries(word);
        if (parts.length == 0) {
            return "";
        }
        return IntStream.range(1, parts.length)
                        .mapToObj(idx -> StringUtils.lowerCase(parts[idx]))
                        .map(StringUtils::capitalize)
                        .collect(joining("", StringUtils.lowerCase(parts[0]), ""));
    }

    /**
     * Returns the argument formatted as SCREAM_CASE.
     */
    public static String screamCase(String word) {
        return Stream.of(splitOnWordBoundaries(word)).map(String::toUpperCase).collect(joining("_"));
    }

    public static String toUpperName(String name) {
        var buf = new StringBuilder(name.length());
        var done = false;
        var prev = 0;
        var cnt = 0;
        while (!done) {
            if (prev >= name.length()) {
                break;
            }
            var idx = findNextUpperEdge(name, prev);
            var value = switch (idx) {
                case -1 -> {
                    done = true;
                    yield name.substring(prev);
                }
                default -> name.substring(prev, idx);

            };
            buf.append(value.toUpperCase(Locale.US));
            if (idx != -1) {
                if (idx > 0 && idx + 1 < name.length()) {
                    buf.append("_").append(Character.toUpperCase(name.charAt(idx)));
                } else if (idx == 0 && idx + 1 < name.length()) {
                    buf.append(Character.toUpperCase(name.charAt(idx)));
                }
            }
            prev = idx + 1;
            if (cnt++ >= 10) {
                break;
            }
        }
        return buf.toString();
    }

    public static String toLowerCamel(String name) {
        var buf = new StringBuilder(name.length());
        if (name.isEmpty()) {
            return name;
        }
        var idx = 0;
        var ch = name.charAt(0);
        if (isUpper(ch)) {
            buf.append(Character.toLowerCase(ch));
            idx = 1;
        }
        while (idx < name.length()) {
            idx = appendLowering(buf, name, idx);
            idx = appendWhileLower(buf, name, idx);
        }
        return buf.toString();
    }

    public static String toUpperCamel(String name) {
        var buf = new StringBuilder(name.length());
        if (name.isEmpty()) {
            return name;
        }
        var idx = 0;
        var ch = name.charAt(0);
        if (!isUpper(ch)) {
            buf.append(Character.toUpperCase(ch));
            idx = 1;
        }
        while (idx < name.length()) {
            idx = appendWhileLower(buf, name, idx);
            idx = appendLowering(buf, name, idx);
        }
        return buf.toString();
    }

    static int appendLowering(StringBuilder buf, String name, int start) {
        var idx = start + 1;
        var curr = name.charAt(start);
        while (idx < name.length()) {
            var prev = name.charAt(idx - 1);
            curr = name.charAt(idx);
            if (isUpper(curr)) {
                buf.append(Character.toLowerCase(prev));
            } else {
                buf.append(prev);
                if (DEBUG) {
                    buf.append("<(1)<");
                }
                return idx;
            }
            ++idx;
        }
        if (curr != 0) {
            buf.append(Character.toLowerCase(curr));
            if (DEBUG) {
                buf.append("<(1.1)<");
            }
        }
        return idx;
    }

    static int appendWhileLower(StringBuilder buf, String name, int start) {
        var idx = start;
        while (idx < name.length()) {
            var curr = name.charAt(idx);
            if (isLower(curr)) {
                buf.append(curr);
            } else {
                buf.append(curr);
                if (DEBUG) {
                    buf.append("<(2)<");
                }
                return idx + 1;
            }
            ++idx;
        }
        if (DEBUG) {
            buf.append("<(2.2)<");
        }
        return idx;
    }

    static int findNextUpperEdge(String name, int start) {
        if (start >= name.length()) {
            return -1;
        }
        var idx = start;
        while (idx + 1 < name.length()) {
            var curr = name.charAt(idx);
            var next = name.charAt(idx + 1);
            if (Character.isUpperCase(curr) && Character.isLowerCase(next)) {
                return idx;
            }
            if (Character.isLowerCase(curr) && Character.isUpperCase(next)) {
                return idx + 1;
            }
            ++idx;
        }
        return -1;
    }

    private static boolean isUpper(char ch) {
        return !Character.isAlphabetic(ch) || !Character.isLowerCase(ch);
    }

    private static boolean isLower(char ch) {
        return !Character.isAlphabetic(ch) || Character.isLowerCase(ch);
    }

}
