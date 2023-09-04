package mx.sugus.codegen.plugin;

import mx.sugus.javapoet.TypeSpec;

public final class TypeSpecResult {
    private final TypeSpec spec;
    private final String namespace;

    TypeSpecResult(Builder builder) {
        this.spec = builder.spec;
        this.namespace = builder.namespace;
    }

    public TypeSpec spec() {
        return spec;
    }

    public String namespace() {
        return namespace;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TypeSpec spec;
        private String namespace;

        public Builder spec(TypeSpec spec) {
            this.spec = spec;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public TypeSpecResult build() {
            return new TypeSpecResult(this);
        }
    }
}
