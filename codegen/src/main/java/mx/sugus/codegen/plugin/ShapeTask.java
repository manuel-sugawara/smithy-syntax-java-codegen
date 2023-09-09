package mx.sugus.codegen.plugin;

import java.util.function.Function;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeTask<T>  extends AbstractShapeTask<T> {
    private final Class<T> outputs;
    private final ShapeType type;
    private final Function<JavaShapeDirective, T> handler;

    ShapeTask(Builder<T> builder) {
        super(builder.clazz, builder.type);
        this.outputs = builder.clazz;
        this.type = builder.type;
        this.handler = builder.handler;
    }

    public static <T> Builder<T> builder(Class<T> clazz) {
        return new Builder<>(clazz);
    }


    @Override
    public Function<JavaShapeDirective, T> produce() {
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
