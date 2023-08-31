package mx.sugus.codegen.util;

import static java.util.stream.Collectors.joining;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import software.amazon.smithy.utils.StringUtils;

public class Name {
    private final String delimiter;
    private final String[] parts;

    public Name(String delimiter, String[] parts) {
        this.delimiter = delimiter;
        this.parts = parts;
    }

    public static Name of(String value) {
        return new Name("", Naming.splitOnWordBoundaries(value));
    }

    @Override
    public String toString() {
        return String.join(delimiter, parts);
    }

    /**
     * Returns the argument formatted as CamelCase.
     */
    public String asCamelCase() {
        return Stream.of(parts).map(StringUtils::lowerCase).map(StringUtils::capitalize).collect(joining());
    }

    /**
     * Returns the argument formatted as pascalCase.
     */
    public String pascalCase() {
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
    public String screamCase() {
        return Stream.of(parts).map(String::toUpperCase).collect(joining("_"));
    }

    public Name toSingularSpelling() {
        var lastIndex = parts.length - 1;
        var lastPart = parts[lastIndex];
        if (lastPart.endsWith("s")) {
            var newParts = parts.clone();
            newParts[parts.length - 1] = lastPart.substring(0, lastPart.length() - 1);
            var result = new Name(delimiter, newParts);
            if (result.toString().equals("case")) {
                return Name.of("aCase");
            }
            return result;
        }
        return this;
    }
}
