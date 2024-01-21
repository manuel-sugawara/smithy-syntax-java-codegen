package mx.sugus.syntax.java;

import java.util.Objects;
import mx.sugus.javapoet.TypeName;

public final class FormatterTypeName implements FormatterNode {
    private final TypeName value;

    private FormatterTypeName(Builder builder) {
        this.value = Objects.requireNonNull(builder.value, "value");
    }

    public SyntaxFormatterNodeKind kind() {
        return SyntaxFormatterNodeKind.TYPE_NAME;
    }

    public TypeName value() {
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
        if (!(obj instanceof FormatterTypeName)) {
            return false;
        }
        FormatterTypeName other = (FormatterTypeName) obj;
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
        return "FormatterTypeName{"
             + "kind: " + kind()
             + ", value: " + value + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TypeName value;

        private boolean _built;

        Builder() {
        }

        Builder(FormatterTypeName data) {
            this.value = data.value;
        }

        public Builder value(TypeName value) {
            this.value = value;
            return this;
        }

        public FormatterTypeName build() {
            return new FormatterTypeName(this);
        }
    }
}
