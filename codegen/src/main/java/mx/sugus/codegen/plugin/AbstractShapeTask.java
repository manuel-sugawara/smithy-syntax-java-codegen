package mx.sugus.codegen.plugin;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import software.amazon.smithy.model.shapes.ShapeType;

public class AbstractShapeTask<T> implements ShapeBaseTask<T> {
    private final Identifier identifier;
    private final Class<T> outputs;
    private final ShapeType type;

    protected AbstractShapeTask(Identifier identifier, Class<T> outputs, ShapeType type) {
        this.identifier = identifier;
        this.outputs = outputs;
        this.type = type;
    }

    protected AbstractShapeTask(Class<T> outputs, ShapeType type) {
        this.identifier = Identifier.of(getClass());
        this.outputs = outputs;
        this.type = type;
    }


    public Identifier taskId() {
        return identifier;
    }

    public Class<T> outputs() {
        return outputs;
    }

    @Override
    public ShapeType type() {
        return type;
    }

    @Override
    public Function<JavaShapeDirective, T> produce() {
        return this::produce;
    }

    @Override
    public BiFunction<JavaShapeDirective, T, T> transform() {
        return this::transform;
    }

    @Override
    public BiConsumer<JavaShapeDirective, T> consume() {
        return this::consume;
    }

    public T produce(JavaShapeDirective directive) {
        throw new UnsupportedOperationException();
    }

    public T transform(JavaShapeDirective directive, T value) {
        throw new UnsupportedOperationException();
    }

    public void consume(JavaShapeDirective directive, T value) {
        throw new UnsupportedOperationException();
    }
}
