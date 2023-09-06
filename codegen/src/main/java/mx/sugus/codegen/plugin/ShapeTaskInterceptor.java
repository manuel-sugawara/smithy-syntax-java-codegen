package mx.sugus.codegen.plugin;

import java.util.function.BiFunction;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeTaskInterceptor<T> {
    private final Class<T> clazz;
    private final ShapeType type;
    private final Identifier taskId;
    private final BiFunction<JavaShapeDirective, T, T> handler;

    ShapeTaskInterceptor(Builder<T> builder) {
        this.clazz = builder.clazz;
        this.type = builder.type;
        this.taskId = builder.taskId;
        this.handler = builder.handler;
    }

    public static <T> Builder<T> builder(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public Class<T> clazz() {
        return clazz;
    }

    public ShapeType type() {
        return type;
    }

    public Identifier taskId() {
        return taskId;
    }

    public BiFunction<JavaShapeDirective, T, T> handler() {
        return handler;
    }

    public static class Builder<T> {
        private Class<T> clazz;
        private ShapeType type;
        private Identifier taskId;
        private BiFunction<JavaShapeDirective, T, T> handler;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> type(ShapeType type) {
            this.type = type;
            return this;
        }

        public Builder<T> taskId(Identifier taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder<T> handler(BiFunction<JavaShapeDirective, T, T> handler) {
            this.handler = handler;
            return this;
        }

        public ShapeTaskInterceptor<T> build() {
            return new ShapeTaskInterceptor<>(this);
        }
    }
}
