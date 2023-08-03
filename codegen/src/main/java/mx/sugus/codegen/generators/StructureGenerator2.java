package mx.sugus.codegen.generators;

import mx.sugus.codegen.spec3.syntax.ClassSyntax;
import mx.sugus.codegen.writer.CodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.StructureShape;

public record StructureGenerator2 (
    Model model,
    Symbol symbol,
    StructureShape shape,
    SymbolProvider symbolProvider,
    WriterDelegator<CodegenWriter> delegator
) {

    public void generate() {
        var spec = generateSpec();
        delegator.useShapeWriter(shape, spec::emit);
    }

    ClassSyntax generateSpec() {
        return null;
    }

}
