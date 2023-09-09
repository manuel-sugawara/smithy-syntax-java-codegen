package mx.sugus.codegen.plugin;

import java.util.function.BiFunction;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeTaskInterceptor<T> extends AbstractShapeTask<T> {
    private final Class<T> clazz;
    private final ShapeType type;
    private final BiFunction<JavaShapeDirective, T, T> handler;

    ShapeTaskInterceptor(Builder<T> builder) {
        super(builder.clazz, builder.type);
        this.clazz = builder.clazz;
        this.type = builder.type;
        this.handler = builder.handler;
    }

    public static <T> Builder<T> builder(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public ShapeType type() {
        return type;
    }

    @Override
    public BiFunction<JavaShapeDirective, T, T> transform() {
        return handler;
    }

    public static class Builder<T> {
        private Class<T> clazz;
        private ShapeType type;
        private BiFunction<JavaShapeDirective, T, T> handler;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> type(ShapeType type) {
            this.type = type;
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
