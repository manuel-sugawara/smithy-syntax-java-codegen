package mx.sugus.syntax.java;

public final class JavaSyntaxOptional {
    private JavaSyntaxOptional(Builder builder) {
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
        if (!(obj instanceof JavaSyntaxOptional)) {
            return false;
        }
        JavaSyntaxOptional other = (JavaSyntaxOptional) obj;
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        return hashCode;
    }

    @Override
    public String toString() {
        return "optional{" + "}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean _built;

        Builder() {
        }

        Builder(JavaSyntaxOptional data) {
        }

        public JavaSyntaxOptional build() {
            return new JavaSyntaxOptional(this);
        }
    }
}
