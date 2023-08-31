package mx.sugus.codegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;

public interface ShapeGeneratorRequest<T extends Shape> {
    Model model();
    Shape shape();
    JavaSymbolProvider symbolProvider();
    Symbol symbol();
}
