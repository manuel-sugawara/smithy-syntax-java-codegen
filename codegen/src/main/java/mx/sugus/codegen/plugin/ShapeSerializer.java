package mx.sugus.codegen.plugin;

import java.util.function.BiConsumer;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeSerializer {
    // why do we need this? It's causing problems and
    // it's not actually used
    private final ShapeType type;
    private final String name;
    private final BiConsumer<JavaShapeDirective, TypeSpec> handler;

    ShapeSerializer(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.handler = builder.handler;
    }

    public ShapeType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public BiConsumer<JavaShapeDirective, TypeSpec> handler() {
        return handler;
    }

    public static class Builder {
        private ShapeType type;
        private String name;
        private BiConsumer<JavaShapeDirective, TypeSpec> handler;

        public Builder type(ShapeType type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder handler(BiConsumer<JavaShapeDirective, TypeSpec> handler) {
            this.handler = handler;
            return this;
        }

        public ShapeSerializer build() {
            return new ShapeSerializer(this);
        }
    }
}
