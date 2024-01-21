package mx.sugus.codegen.util;

import software.amazon.smithy.utils.StringUtils;

public class SimpleName {
    private final NameCasing casing;
    private final String[] parts;

    private SimpleName(NameCasing casing, String[] parts) {
        this.casing = casing;
        this.parts = casing.convert(parts);
    }

    public static SimpleName of(NameCasing casing, String value) {
        return new SimpleName(casing, Naming.splitOnWordBoundaries(value));
    }

    public static SimpleName of(NameCasing casing, String[] parts) {
        return new SimpleName(casing, parts);
    }

    @Override
    public String toString() {
        return String.join(casing.delimiter(), parts);
    }

    /**
     * Returns the argument converted to the given casing.
     */
    public SimpleName convertTo(NameCasing casing) {
        if (this.casing == casing) {
            return this;
        }
        return new SimpleName(casing, this.parts);
    }

    public SimpleName toSingularSpelling() {
        var lastIndex = parts.length - 1;
        var lastPart = parts[lastIndex];
        var lastChar = lastPart.charAt(lastPart.length() - 1);
        if ((lastChar == 's' || lastChar == 'S') && lastPart.length() > 1) {
            var newParts = parts.clone();
            newParts[parts.length - 1] = lastPart.substring(0, lastPart.length() - 1);
            return new SimpleName(casing, newParts);
        }
        return this;
    }

    public SimpleName toPluralSpelling() {
        var lastIndex = parts.length - 1;
        var lastPart = parts[lastIndex];
        var lastChar = lastPart.charAt(lastPart.length() - 1);
        if (!((lastChar == 's' || lastChar == 'S') && lastPart.length() > 1)) {
            var newParts = parts.clone();
            newParts[parts.length - 1] = lastPart + "s";
            return new SimpleName(casing, newParts);
        }
        return this;
    }

    public SimpleName withPrefix(String prefix) {
        var newParts = new String[this.parts.length + 1];
        newParts[0] = prefix;
        System.arraycopy(parts, 0, newParts, 1, this.parts.length);
        return new SimpleName(casing, newParts);
    }

    public enum NameCasing {
        CAMEL("") {
            @Override
            public String[] convert(String[] parts) {
                if (parts.length == 0) {
                    return parts;
                }
                var newParts = parts.clone();
                for (var idx = 0; idx < newParts.length; idx++) {
                    var value = newParts[idx];
                    newParts[idx] = StringUtils.capitalize(StringUtils.lowerCase(value));
                }
                return newParts;
            }
        },
        PASCAL("") {
            @Override
            public String[] convert(String[] parts) {
                if (parts.length == 0) {
                    return parts;
                }
                var newParts = parts.clone();
                newParts[0] = StringUtils.lowerCase(newParts[0]);
                for (var idx = 1; idx < newParts.length; idx++) {
                    var value = newParts[idx];
                    newParts[idx] = StringUtils.capitalize(StringUtils.lowerCase(value));
                }
                return newParts;
            }
        },
        SCREAMING("_") {
            @Override
            public String[] convert(String[] parts) {
                if (parts.length == 0) {
                    return parts;
                }
                var newParts = parts.clone();
                for (var idx = 0; idx < newParts.length; idx++) {
                    var value = newParts[idx];
                    newParts[idx] = StringUtils.upperCase(value);
                }
                return newParts;
            }
        };

        private final String delimiter;

        NameCasing(String separator) {
            this.delimiter = separator;
        }

        public String delimiter() {
            return delimiter;
        }

        public abstract String[] convert(String[] parts);
    }
}
