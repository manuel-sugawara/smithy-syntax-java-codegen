package mx.sugus.codegen;

import software.amazon.smithy.model.shapes.Shape;

public interface ShapeGenerator<T extends Shape> {
    ShapeGeneratorResult<T> generate(ShapeGeneratorRequest<T> request);
}
