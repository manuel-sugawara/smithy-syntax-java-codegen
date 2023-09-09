package mx.sugus.codegen.plugin;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import software.amazon.smithy.model.shapes.ShapeType;

public interface ShapeBaseTask<T> {

    Identifier taskId();

    Class<T> outputs();

    ShapeType type();

    default Function<JavaShapeDirective, T> produce() {
        throw new UnsupportedOperationException();
    }

    default BiFunction<JavaShapeDirective, T, T> transform() {
        throw new UnsupportedOperationException();
    }

    default BiConsumer<JavaShapeDirective, T> consume() {
        throw new UnsupportedOperationException();
    }
}
