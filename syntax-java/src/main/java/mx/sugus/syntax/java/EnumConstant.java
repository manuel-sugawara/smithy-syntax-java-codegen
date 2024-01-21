package mx.sugus.syntax.java;

import java.util.Objects;

public final class EnumConstant implements SyntaxNode {
    private final String name;

    private final String value;

    private EnumConstant(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.value = Objects.requireNonNull(builder.value, "value");
    }

    public String name() {
        return this.name;
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
        if (!(obj instanceof EnumConstant)) {
            return false;
        }
        EnumConstant other = (EnumConstant) obj;
        return this.name.equals(other.name)
             && this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + value.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "EnumConstant{"
             + "name: " + name
             + ", value: " + value + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitEnumConstant(this);
    }

    public static final class Builder {
        private String name;

        private String value;

        private boolean _built;

        Builder() {
        }

        Builder(EnumConstant data) {
            this.name = data.name;
            this.value = data.value;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public EnumConstant build() {
            return new EnumConstant(this);
        }
    }
}
