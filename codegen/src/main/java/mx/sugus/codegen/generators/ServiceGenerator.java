package mx.sugus.codegen.generators;

import mx.sugus.codegen.JavaSymbolProvider;
import mx.sugus.codegen.generators.internal.GenerateRewriteVisitor;
import mx.sugus.codegen.generators.internal.GenerateVisitor;
import mx.sugus.codegen.util.PoetUtils;
import mx.sugus.codegen.writer.CodegenWriter;
import mx.sugus.javapoet.TypeSpec;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.ServiceShape;

public record ServiceGenerator(
    Model model,
    Symbol symbol,
    ServiceShape shape,
    JavaSymbolProvider symbolProvider,
    WriterDelegator<CodegenWriter> delegator,
    String namespace
) {
    public void generate() {
        var spec = generateVisitor();
        if (spec != null) {
            var ns = namespace;
            var file = ns.replace(".", "/") + "/" + spec.name + ".java";
            delegator.useFileWriter(file, w -> PoetUtils.emit(w, spec, namespace));
        }
        var rewriteSpec = generateRewriteVisitor();
        if (rewriteSpec != null) {
            var ns = namespace;
            var file = ns.replace(".", "/") + "/" + rewriteSpec.name + ".java";
            delegator.useFileWriter(file, w -> PoetUtils.emit(w, rewriteSpec, namespace));
        }
    }

    TypeSpec generateVisitor() {
        return new GenerateVisitor("SyntaxNode")
            .generate(this);
    }

    TypeSpec generateRewriteVisitor() {
        return new GenerateRewriteVisitor("SyntaxNode")
            .generate(this);
    }
}
