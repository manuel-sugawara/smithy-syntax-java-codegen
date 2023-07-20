package mx.sugus.codegen.writer.sections;

import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record FieldSection(Shape shape) implements CodeSection {
}
