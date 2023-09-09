package mx.sugus.codegen.plugin;

import java.util.function.BiConsumer;
import software.amazon.smithy.model.shapes.ShapeType;

public final class ShapeSerializer<T> extends AbstractShapeTask<T> {
    private final Class<T> clazz;
    private final Identifier identifier;
    // why do we need this? It's causing problems and
    // it's not actually used
    private final ShapeType type;
    private final String name;
    private final BiConsumer<JavaShapeDirective, T> handler;

    ShapeSerializer(Builder<T> builder) {
        super(builder.clazz, builder.type);
        this.clazz = builder.clazz;
        this.identifier = builder.identifier;
        this.type = builder.type;
        this.name = builder.name;
        this.handler = builder.handler;
    }

    public static <T> Builder<T> builder(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public Class<T> clazz() {
        return clazz;
    }

    public Identifier identifier() {
        return identifier;
    }

    public ShapeType type() {
        return type;
    }

    public String name() {
        return name;
    }

    @Override
    public BiConsumer<JavaShapeDirective, T> consume() {
        return handler;
    }

    public static class Builder<T> {
        private Class<T> clazz;
        private Identifier identifier;
        private ShapeType type;
        private String name;
        private BiConsumer<JavaShapeDirective, T> handler;

        public Builder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> type(ShapeType type) {
            this.type = type;
            return this;
        }

        public Builder<T> name(String name) {
            this.name = name;
            return this;
        }

        public Builder<T> handler(BiConsumer<JavaShapeDirective, T> handler) {
            this.handler = handler;
            return this;
        }

        public Builder<T> identifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public ShapeSerializer<T> build() {
            return new ShapeSerializer<>(this);
        }
    }
}
