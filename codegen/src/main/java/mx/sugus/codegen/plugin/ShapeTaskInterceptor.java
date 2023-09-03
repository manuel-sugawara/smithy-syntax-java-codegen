package mx.sugus.codegen.plugin;

import java.util.function.BiFunction;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeType;

public class ShapeTaskInterceptor {
    private final ShapeType type;
    private final String name;
    private final BiFunction<JavaShapeDirective, TypeSpec, TypeSpec> handler;


    ShapeTaskInterceptor(ShapeType type, String name, BiFunction<JavaShapeDirective, TypeSpec, TypeSpec> handler) {
        this.type = type;
        this.name = name;
        this.handler = handler;
    }

    ShapeTaskInterceptor(Builder builder) {
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

    public BiFunction<JavaShapeDirective, TypeSpec, TypeSpec> handler() {
        return handler;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ShapeType type;
        private String name;
        private BiFunction<JavaShapeDirective, TypeSpec, TypeSpec> handler;

        public Builder type(ShapeType type) {
            this.type = type;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder handler(BiFunction<JavaShapeDirective, TypeSpec, TypeSpec> handler) {
            this.handler = handler;
            return this;
        }

        public ShapeTaskInterceptor build() {
            return new ShapeTaskInterceptor(this);
        }
    }
}
