package mx.sugus.syntax.java;

import java.util.Objects;

public final class FormatterName implements FormatterNode {
    private final Object value;

    private FormatterName(Builder builder) {
        this.value = Objects.requireNonNull(builder.value, "value");
    }

    public SyntaxFormatterNodeKind kind() {
        return SyntaxFormatterNodeKind.NAME;
    }

    public Object value() {
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
        if (!(obj instanceof FormatterName)) {
            return false;
        }
        FormatterName other = (FormatterName) obj;
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
        return "FormatterName{"
             + "kind: " + kind()
             + ", value: " + value + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Object value;

        private boolean _built;

        Builder() {
        }

        Builder(FormatterName data) {
            this.value = data.value;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public FormatterName build() {
            return new FormatterName(this);
        }
    }
}
