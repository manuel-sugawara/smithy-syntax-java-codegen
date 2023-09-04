package mx.sugus.codegen.plugin;

import java.util.function.Function;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeTask<T> {
    private final Class<T> clazz;
    private final ShapeType type;
    private final Identifier identifier;
    private final Function<JavaShapeDirective, T> handler;

    ShapeTask(Builder<T> builder) {
        this.clazz = builder.clazz;
        this.type = builder.type;
        this.identifier = builder.identifier;
        this.handler = builder.handler;
    }

    public static Identifier of(String value) {
        ShapeId shapeId = ShapeId.from(value);
        return new Identifier(shapeId.getNamespace(), shapeId.getName());
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
        return identifier;
    }

    public Function<JavaShapeDirective, T> handler() {
        return handler;
    }

    public static class Builder<T> {
        private Class<T> clazz;

        private ShapeType type;
        private Identifier identifier;
        private Function<JavaShapeDirective, T> handler;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> type(ShapeType type) {
            this.type = type;
            return this;
        }

        public Builder<T> taskId(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder<T> handler(Function<JavaShapeDirective, T> init) {
            this.handler = init;
            return this;
        }

        public ShapeTask<T> build() {
            return new ShapeTask<>(this);
        }
    }
}
