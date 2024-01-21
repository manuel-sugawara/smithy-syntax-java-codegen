package mx.sugus.syntax.java;

import java.util.Objects;
import mx.sugus.javapoet.TypeName;

public final class Parameter implements SyntaxNode {
    private final String name;

    private final TypeName type;

    private Parameter(Builder builder) {
        this.name = Objects.requireNonNull(builder.name, "name");
        this.type = Objects.requireNonNull(builder.type, "type");
    }

    public String name() {
        return this.name;
    }

    public TypeName type() {
        return this.type;
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
        if (!(obj instanceof Parameter)) {
            return false;
        }
        Parameter other = (Parameter) obj;
        return this.name.equals(other.name)
             && this.type.equals(other.type);
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode = 31 * hashCode + name.hashCode();
        hashCode = 31 * hashCode + type.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return "Parameter{"
             + "name: " + name
             + ", type: " + type + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public <T> T accept(SyntaxNodeVisitor<T> visitor) {
        return visitor.visitParameter(this);
    }

    public static final class Builder {
        private String name;

        private TypeName type;

        private boolean _built;

        Builder() {
        }

        Builder(Parameter data) {
            this.name = data.name;
            this.type = data.type;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(TypeName type) {
            this.type = type;
            return this;
        }

        public Parameter build() {
            return new Parameter(this);
        }
    }
}
