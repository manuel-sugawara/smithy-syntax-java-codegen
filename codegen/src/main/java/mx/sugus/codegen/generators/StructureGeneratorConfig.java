package mx.sugus.codegen.generators;

public final class StructureGeneratorConfig {
    private final boolean enableBuilderInterface;
    private final boolean enableBuilderPojoMethods;

    StructureGeneratorConfig(Builder builder) {
        this.enableBuilderInterface = builder.enableBuilderInterface;
        this.enableBuilderPojoMethods = builder.enableBuilderPojoMethods;
    }

    public boolean enableBuilderInterface() {
        return enableBuilderInterface;
    }

    public boolean enableBuilderPojoMethods() {
        return enableBuilderPojoMethods;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean enableBuilderInterface;
        private boolean enableBuilderPojoMethods;

        public Builder() {
        }

        public Builder enableBuilderInterface(boolean enableBuilderInterface) {
            this.enableBuilderInterface = enableBuilderInterface;
            return this;
        }

        public Builder enableBuilderPojoMethods(boolean enableBuilderPojoMethods) {
            this.enableBuilderPojoMethods = enableBuilderPojoMethods;
            return this;
        }

        public StructureGeneratorConfig build() {
            return new StructureGeneratorConfig(this);
        }
    }
}
