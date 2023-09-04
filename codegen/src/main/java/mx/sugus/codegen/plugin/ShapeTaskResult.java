package mx.sugus.codegen.plugin;

public final class ShapeTaskResult<T> {
    private final T result;

    ShapeTaskResult(Builder<T> builder) {
        this.result = builder.result;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private T result;

        public Builder<T> result(T result) {
            this.result = result;
            return this;
        }

        public ShapeTaskResult<T> build() {
            return new ShapeTaskResult<>(this);
        }
    }
}
