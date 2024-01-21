package mx.sugus.syntax.java;

import java.util.Objects;

public final class FormatterString implements FormatterNode {
    private final String value;

    private FormatterString(Builder builder) {
        this.value = Objects.requireNonNull(builder.value, "value");
    }

    public SyntaxFormatterNodeKind kind() {
        return SyntaxFormatterNodeKind.STRING;
    }

    public String value() {
        return this.value;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FormatterString)) {
            return false;
        }
        FormatterString other = (FormatterString) obj;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + Objects.hashCode(this.kind());
        hashCode = 31 * hashCode + value.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "FormatterString{"
             + "kind: " + kind()
             + ", value: " + value + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String value;

        private boolean _built;

        Builder() {
        }

        Builder(FormatterString data) {
            this.value = data.value;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public FormatterString build() {
            return new FormatterString(this);
        }
    }
}
