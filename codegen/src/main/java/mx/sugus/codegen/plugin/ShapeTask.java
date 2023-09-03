package mx.sugus.codegen.plugin;

import java.util.function.Function;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeTask {
    private final ShapeType type;
    private final Identifier identifier;
    private final Function<JavaShapeDirective, TypeSpec> handler;

    ShapeTask(Builder builder) {
        this.type = builder.type;
        this.identifier = builder.identifier;
        this.handler = builder.handler;
    }

    public static Identifier of(String value) {
        ShapeId shapeId = ShapeId.from(value);
        return new Identifier(shapeId.getNamespace(), shapeId.getName());
    }

    public ShapeType type() {
        return type;
    }

    public Identifier taskId() {
        return identifier;
    }

    public Function<JavaShapeDirective, TypeSpec> handler() {
        return handler;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ShapeType type;
        private Identifier identifier;
        private Function<JavaShapeDirective, TypeSpec> handler;

        public Builder type(ShapeType type) {
            this.type = type;
            return this;
        }

        public Builder taskId(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder handler(Function<JavaShapeDirective, TypeSpec> init) {
            this.handler = init;
            return this;
        }

        public ShapeTask build() {
            return new ShapeTask(this);
        }
    }
}
