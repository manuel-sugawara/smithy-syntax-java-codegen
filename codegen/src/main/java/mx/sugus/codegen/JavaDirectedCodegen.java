package mx.sugus.codegen;

import mx.sugus.codegen.generators.EnumGenerator;
import mx.sugus.codegen.generators.ErrorGenerator;
import mx.sugus.codegen.generators.IntEnumGenerator;
import mx.sugus.codegen.generators.StructureGenerator;
import mx.sugus.codegen.generators.UnionGenerator;
import mx.sugus.codegen.integration.JavaCodegenIntegration;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.codegen.core.directed.DirectedCodegen;
import software.amazon.smithy.codegen.core.directed.GenerateEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateErrorDirective;
import software.amazon.smithy.codegen.core.directed.GenerateIntEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.codegen.core.directed.GenerateUnionDirective;


public final class JavaDirectedCodegen
    implements DirectedCodegen<JavaCodegenContext, JavaCodegenSettings, JavaCodegenIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<JavaCodegenSettings> directive) {
        return SymbolProvider.cache(JavaSymbolProvider.create(directive));
    }

    @Override
    public JavaCodegenContext createContext(CreateContextDirective<JavaCodegenSettings, JavaCodegenIntegration> directive) {
        return JavaCodegenContext.fromContextDirective(directive);
    }

    @Override
    public void generateService(GenerateServiceDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
    }

    @Override
    public void generateStructure(GenerateStructureDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        new StructureGenerator(
            directive.model(),
            directive.symbol(),
            directive.shape(),
            directive.symbolProvider(),
            directive.context().writerDelegator()
        ).generate();
    }

    @Override
    public void generateError(GenerateErrorDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        new ErrorGenerator(
            directive.model(),
            directive.symbol(),
            directive.shape(),
            directive.symbolProvider(),
            directive.context().writerDelegator()
        ).generate();
    }

    @Override
    public void generateUnion(GenerateUnionDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        new UnionGenerator(
            directive.model(),
            directive.symbol(),
            directive.shape(),
            directive.symbolProvider(),
            directive.context().writerDelegator()
        ).generate();
    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        new EnumGenerator(
            directive.model(),
            directive.symbol(),
            directive.shape().asEnumShape().orElseThrow(),
            directive.symbolProvider(),
            directive.context().writerDelegator()
        ).generate();
    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<JavaCodegenContext, JavaCodegenSettings> directive) {
        new IntEnumGenerator(
            directive.model(),
            directive.symbol(),
            directive.shape().asIntEnumShape().orElseThrow(),
            directive.symbolProvider(),
            directive.context().writerDelegator()
        ).generate();
    }
}
